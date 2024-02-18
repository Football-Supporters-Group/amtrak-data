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

import com.wolginm.amtrak.data.properties.AmtrakProperties;
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
    public final static String TMP_FILE_PREFIX = "amtk_data";
    private final Path tmpDir;
    private final File tmpDirDeleteRecord;
    FileUtil() {
        try {
            tmpDir = Files.createTempDirectory(TMP_FILE_PREFIX);
            tmpDirDeleteRecord = tmpDir.toFile();
            tmpDirDeleteRecord.deleteOnExit();
            log.debug("AMTK-2101: Marked tmp file for deletion on JVM close [{}]", tmpDir.toString());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

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
     * @param current
     * @return
     * @throws IOException
     */
    public boolean tearDownRecursive(File current) throws IOException {
        for (String nextSuffix: current.list()) {
            this.tearDownRecursive(current, nextSuffix);
        }
        Files.delete(current.toPath());
        return true;
    }

    /**
     * Recursion delete.
     * @param previous
     * @param name
     * @return
     * @throws IOException
     */
    private boolean tearDownRecursive(File previous, String name) throws IOException {
        File current = new File(previous, name);
        if (current.isDirectory()) {
            for (String next: current.list()) {
                this.tearDownRecursive(current, next);
            }
        }
        Files.delete(current.toPath());
        return true;
    }
}
