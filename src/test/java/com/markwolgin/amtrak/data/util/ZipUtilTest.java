package com.markwolgin.amtrak.data.util;

import com.markwolgin.amtrak.data.configuration.TemporaryDirectoryConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;

@Slf4j
class ZipUtilTest {

    private final ZipUtil zipUtil = new ZipUtil();
    private final ClassLoader classLoader = this.getClass().getClassLoader();
    private final Path slimZip = Path.of(classLoader.getResource("zip/gtfs_slim.zip").toURI());
    private Path destinationPath;

    ZipUtilTest() throws URISyntaxException {
    }

    @BeforeEach
    void setUp() throws IOException {
        destinationPath = Files.createTempDirectory(TemporaryDirectoryConfiguration.TMP_FILE_PREFIX);
        destinationPath.toFile().deleteOnExit();
    }

    @AfterEach
    void tearDown() throws IOException {
        this.tearDownRecursive(destinationPath.toFile());
    }

    @Test
    void unzip() {
    }

    @Test
    void testUnzip() throws IOException {
        Assertions.assertDoesNotThrow(() -> this.zipUtil.unzip(slimZip.toString(), destinationPath.toString()));
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
        log.info("AMTK-0000: Deleting [{}]", current.toPath());
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
        log.info("AMTK-0000: Deleting [{}]", current.toPath());
        Files.delete(current.toPath());
        return true;
    }
}