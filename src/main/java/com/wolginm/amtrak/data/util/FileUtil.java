package com.wolginm.amtrak.data.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.FileSystems;
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
     * @param path
     * @return
     */
    public int prepFoldersForFile(final Path path) {
        return this.prepFoldersForFile(path, 0);
    }

    private int prepFoldersForFile(final Path path, int depth) {
        File current = path.getParent().toFile();
        if (current.exists()) {
            
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

    public void dataBufferUtilWrite(final Flux<DataBuffer> flux,
                                          final Path path) {
        log.info("AMTK-");
        DataBufferUtils.write(flux,
                path,
                StandardOpenOption.CREATE).block();
    }
}
