package com.wolginm.amtrak.data.util;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileSystemException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class ZipUtil {
    
    private final int BUFFER_SIZE = 4096;

    public void unzip(final byte[] data, final String destinationUrl) throws IOException {
        File destinationDir = new File(destinationUrl);
        if (!destinationDir.exists()) {
            destinationDir.mkdir();
        }

        ZipInputStream zipInputStream = 
            new ZipInputStream(new ByteArrayInputStream(data));
        ZipEntry zipEntry = zipInputStream.getNextEntry();

        this.unzipLoop(zipInputStream, zipEntry, destinationDir);
    }

    public void unzip(final String sourceUrl, final String destinationUrl) throws IOException {
        log.info("AMTK-I-6400: Attempting to unzip file [{}] to [{}]", sourceUrl, destinationUrl);
        File destinationDir = new File(destinationUrl);
        if (!destinationDir.exists()) {
            destinationDir.mkdir();
        }

        ZipInputStream zipInputStream = 
            new ZipInputStream(new FileInputStream(sourceUrl));
        ZipEntry zipEntry = zipInputStream.getNextEntry();
        
        this.unzipLoop(zipInputStream, zipEntry, destinationDir);
        log.info("AMTK-I-6401: Unzip file to [{}]", destinationUrl);
    }

    private void unzipLoop(ZipInputStream zipInputStream, ZipEntry zipEntry, File destinationDir) throws IOException {
        String filePath;
        File directory;

        while (zipEntry != null) {
            filePath = String.format("%s%s%s",
                destinationDir, File.separator, zipEntry.getName());
            if (!zipEntry.isDirectory()) {
                this.extractFile(zipInputStream,
                    filePath);
            } else {
                directory = new File(filePath);
                if (!directory.mkdir()) throw new FileSystemException("Unable to create directory [%s]".formatted(filePath));
            }
            zipInputStream.closeEntry();
            zipEntry = zipInputStream.getNextEntry();
        }
    }

    /**
     * Inflates the zip file
     * @param zipInputStream
     * @param filePath
     * @return Status of the extraction
     */
    private boolean extractFile(ZipInputStream zipInputStream, String filePath) throws IOException{
        boolean status = false;

        try {
            this.writeFileToDisk(zipInputStream, filePath);
            log.debug("AMTK-I-6400: Extracted file [{}]", filePath);
            status = true;
        } catch (IOException e) {
            log.error("AMTK-I-6499: Unable to extract file [{}]", filePath);
            log.error(e.getMessage());
            throw e;
        }
        
        return status;
    }

    private int writeFileToDisk(InputStream inputStream, String filePath) throws IOException {
        BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(filePath));
        byte[] bytesIn = new byte[BUFFER_SIZE];
        int read = 0;

        //Reads in 4096 byte chunks
        while ((read = inputStream.read(bytesIn)) != -1) {
            bufferedOutputStream.write(bytesIn, 0, read);
        }
        bufferedOutputStream.close();
        return 0;
    }
}
