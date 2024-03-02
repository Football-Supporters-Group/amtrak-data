package com.markwolgin.amtrak.data.util;

import java.io.*;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.zip.CRC32;
import java.util.zip.CheckedInputStream;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;

@Slf4j
@Component
public class FileUtil {

    private final int BUFFER_SIZE = 4096;
    private final Path tmpDir;

    public FileUtil(@Qualifier("temporaryDirectoryPath") final Path tempDir) {
        this.tmpDir = tempDir;
    }

    /**
     * Gets the temporary directory path.
     * @return  Temp directory path.
     * @deprecated Use {@code @Qualifier('temporaryDirectoryPath') final Path temporaryDirectory}
     */
    @Deprecated
    public Path getTempDirectory() {
        return this.tmpDir;
    }



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
            saveLocation
                    = Files.createDirectories(Path.of(this.tmpDir.toString(), pathSuffix));
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
            var err = "AMTK-6299: Failure to calculate a checksum for file [%s]".formatted(checkedFile.toPath().toString());
            log.error(err);
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

    /**
     * Recursion entry point to delete recursively.
     * @param current   The current file.  Will be deleted.
     * @return          True
     * @throws IOException  An error occurred.
     */
    public boolean tearDownRecursive(File current) throws IOException {
        for (String nextSuffix: current.list()) {
            this.tearDownRecursive(current, nextSuffix);
        }
        log.debug("AMTK-6230: Deleting [{}]", current.toPath());
        Files.delete(current.toPath());
        return true;
    }

    /**
     * Recursion delete.
     * @param previous  The parent file.
     * @param name      The next path suffix.
     * @return          True
     * @throws IOException  An error occurred.
     */
    private boolean tearDownRecursive(File previous, String name) throws IOException {
        File current = new File(previous, name);
        if (current.isDirectory()) {
            for (String next: current.list()) {
                this.tearDownRecursive(current, next);
            }
        }
        log.debug("AMTK-6230: Deleting [{}]", current.toPath());
        Files.delete(current.toPath());
        return true;
    }
}
