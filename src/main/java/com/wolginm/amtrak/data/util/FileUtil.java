package com.wolginm.amtrak.data.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.zip.CRC32;
import java.util.zip.CheckedInputStream;

import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

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

    /**
     * Assumes the path is starting as a file
     * @param pathSuffix    The path to save the tmp files to.
     * @return              Files Path.
     */
    public Path prepFoldersForFile(final String pathSuffix) {
        Path saveLocation;
        try {
            saveLocation = Files.createTempDirectory(pathSuffix);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return saveLocation;
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

    public void dataBufferUtilWrite(final Flux<DataBuffer> flux,
                                          final Path path) {
        log.info("AMTK-6200: Attempting to save DataBuffer to Path [{}]", path.toAbsolutePath());
        DataBufferUtils.write(flux,
                path,
                StandardOpenOption.CREATE).block();
    }
}
