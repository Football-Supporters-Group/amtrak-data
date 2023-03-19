package com.wolginm.amtrak.data.service;

import com.wolginm.amtrak.data.client.AmtrakRestTemplate;
import com.wolginm.amtrak.data.exception.NotFileException;
import com.wolginm.amtrak.data.properties.AmtrakProperties;
import com.wolginm.amtrak.data.util.FileUtil;
import com.wolginm.amtrak.data.util.ZipUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Iterator;

@Slf4j
@Service
public class AmtrakGTFSService {

    private AmtrakProperties _amtrakProperties;
    private AmtrakRestTemplate _amtrakRestTemplate;
    private FileUtil _fileUtil;
    private ZipUtil _zipUtil;

    @Autowired
    public AmtrakGTFSService(AmtrakRestTemplate amtrakRestTemplate,
                             AmtrakProperties amtrakProperties,
                             FileUtil _fileUtil,
                             ZipUtil zipUtil) {
        this._amtrakRestTemplate = amtrakRestTemplate;
        this._amtrakProperties = amtrakProperties;
        this._fileUtil = _fileUtil;
        this._zipUtil = zipUtil;
    }

    public boolean downloadAmtrakZipFile() throws IOException {
        this._amtrakRestTemplate.downloadGTFSFile(this._amtrakProperties.getTempFile());
        this._zipUtil.unzip(this._amtrakProperties.getTempFile(),
                this._amtrakProperties.getTempDirectory());
        this._fileUtil.deleteFile(new File(this._amtrakProperties.getTempFile()));
        return true;
    }

    public boolean updateAmtrakDataFiles() throws IOException {
        this.downloadAmtrakZipFile();
        if (this._isWorkDirectoryOld()) {
            this._fileUtil.deleteAllFilesInFolder(new File(this._amtrakProperties.getWorkDirectory()), true);
        } else {
            this._fileUtil.moveAllFiles(new File(this._amtrakProperties.getTempDirectory()),
                    new File(this._amtrakProperties.getWorkDirectory()),
                    true);
        }


    }

    private boolean _isWorkDirectoryOld() throws IOException {
        File tempFile, workFile;
        boolean needToUpdate = false;

        try (DirectoryStream<Path> stream = Files.newDirectoryStream(new File(this._amtrakProperties.getTempDirectory()).toPath())) {
            Iterator<Path> iterator = stream.iterator();
            if (iterator.hasNext()) {
                tempFile = iterator.next().toFile();
            } else {
                throw new NotFileException(this._amtrakProperties.getTempDirectory());
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(new File(this._amtrakProperties.getWorkDirectory()).toPath())) {
            Iterator<Path> iterator = stream.iterator();
            if (iterator.hasNext()) {
                workFile = iterator.next().toFile();
            } else {
                throw new NotFileException(this._amtrakProperties.getWorkDirectory());
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        int comparison = this._fileUtil.compareFileTimes(tempFile, workFile);
        if (comparison > 0) {
            log.debug("Files are out of date and need to be rotated.");
            needToUpdate = true;
        }

        return needToUpdate;
    }
}
