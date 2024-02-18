package com.wolginm.amtrak.data.util;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;


@Slf4j
@ExtendWith(MockitoExtension.class)
class FileUtilTest {

    private final FileUtil fileUtil = new FileUtil();

    private final String tmpDirsLocation = System.getProperty("java.io.tmpdir");

    @Nested
    @DisplayName("Pathing Tests")
    class PathingTests {

        private Path pathToTmpDir;
        private String innerPath = "innerTmpPath";

        @BeforeEach
        void setUp() {

        }

        @AfterEach
        void tearDown() {
            if (pathToTmpDir == null) {
                return;
            }
            try {
                for (String next: pathToTmpDir.toFile().list()) {
                    tearDownRecursive(pathToTmpDir.toFile(), next);
                }
                pathToTmpDir = null;
            } catch (IOException e) {
                var err = "AMTK-0000: Could not tear down path [%s] and all of its recursive elements in system tmp directory for testing FileUtil.java".formatted(pathToTmpDir.toString());
                log.error(err);
                throw new RuntimeException(err, e);
            }
        }

        @Test
        void prepFoldersForFile_Simple() {
            Path actual = fileUtil.prepFoldersForFile(innerPath);

            Assertions.assertNotNull(actual);
            Assertions.assertTrue(actual.toFile().exists());
            this.pathToTmpDir = fileUtil.getTempDirectory();
        }

        @Test
        void prepFoldersForFile_Complex() {
            Path actual = fileUtil.prepFoldersForFile(innerPath.concat("/%s".formatted("ABC")));

            Assertions.assertNotNull(actual);
            Assertions.assertTrue(actual.toFile().exists());
            this.pathToTmpDir = fileUtil.getTempDirectory();
        }

        @Test
        void prepFoldersForFile_WithDelete() throws IOException {
            Path actual = fileUtil.prepFoldersForFile(innerPath);

            Assertions.assertNotNull(actual);
            Assertions.assertTrue(actual.toFile().exists());

            fileUtil.tearDownRecursive(actual.toFile());

            Assertions.assertFalse(actual.toFile().exists());
        }

        @Test
        void prepFoldersForFile_Complex_WithDelete() throws IOException {
            Path actual = fileUtil.prepFoldersForFile(innerPath);

            Assertions.assertNotNull(actual);
            Assertions.assertTrue(actual.toFile().exists());

            fileUtil.tearDownRecursive(actual.toFile());

            Assertions.assertFalse(actual.toFile().exists());
        }


    }

    @Test
    void resolvePath() {

    }



    @Test
    void calcualteChecksum() {
    }

    @Test
    void dataBufferUtilWrite() {
    }

    /**
     * Recursion entry point to delete recursively.
     * @param current
     * @return
     * @throws IOException
     */
    private boolean tearDownRecursive(File current) throws IOException {
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