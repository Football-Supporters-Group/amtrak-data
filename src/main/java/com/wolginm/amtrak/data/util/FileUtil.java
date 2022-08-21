package com.wolginm.amtrak.data.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.time.LocalTime;
import java.util.zip.CRC32;
import java.util.zip.CheckedInputStream;

import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class FileUtil {

    private final int BUFFER_SIZE = 4096;

    public Path resolvePath(final String path) {
        return FileSystems.getDefault()
            .getPath(path)
            .normalize()
            .toAbsolutePath();
    }

    public boolean directoryExists(final Path path) {
        File current = path.getParent().toFile();
        return current.exists();
    } 

    public long getAgeInMili(final Path path) throws IOException {
        long miliSec = -1;

        if (this.directoryExists(path)) {
            File current = path.getParent().toFile();

            miliSec = current.getCanonicalFile().lastModified();
        }
        log.debug("File {} age: {}", path.getFileName(), miliSec);
        return miliSec;
    }

    /**
     * Return -1 if less then compared timeout.  
     * 0 if equal
     * 1 if greater then
     * @param time
     * @param timeout
     * @return
     */
    public int compareAgeToConstantTime(final long time, final long timeout) {
        LocalTime localTime = LocalTime.now();
        long comparable = time - (localTime.getNano() / 1000);

        return comparable > 0.0 ? 1 : -1;
    }

    /**
     * Assumes the path is starting as a file
     * @param path
     * @return
     */
    public int prepFoldersForFile(final Path path) {
        return this.prepFoldersForFile(path, 0);
    }

    private int prepFoldersForFile(final Path path, int depth) {
        File current = path.getParent().toFile();
        if (current.exists()) {
            log.info("Path exists!");
        } else {
            log.info("At directory: {}", path.toString());
            this.prepFoldersForFile(path.getParent(), depth ++);
            current.mkdir();
            log.info("Directory created: {}", path.toString());
        }
        return depth;
    }

    
    public long calcualteChecksum(File checkedFile) {
        long checksum = -1;
        try (CheckedInputStream checkedInputStream 
            = new CheckedInputStream(new FileInputStream(checkedFile), new CRC32())){
                
            byte[] buffer = new byte[this.BUFFER_SIZE];
            while (checkedInputStream.read(buffer, 0, buffer.length) >= 0) {
            }
            checksum = checkedInputStream.getChecksum().getValue();
        } catch (IOException e) {
            log.error(e.getMessage());
        }
        return checksum;
        
    }
}
