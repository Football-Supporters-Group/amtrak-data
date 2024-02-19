package com.wolginm.amtrak.data.util;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import reactor.core.publisher.Flux;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.*;

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
            Assertions.assertTrue(actual.startsWith(fileUtil.getTempDirectory()));
            Assertions.assertTrue(actual.toFile().exists());
            this.pathToTmpDir = fileUtil.getTempDirectory();
            actual.toFile().deleteOnExit();
        }

        @Test
        void prepFoldersForFile_Complex() {
            Path actual = fileUtil.prepFoldersForFile(innerPath.concat("/%s".formatted("ABC")));

            Assertions.assertNotNull(actual);
            Assertions.assertTrue(actual.startsWith(fileUtil.getTempDirectory()));
            Assertions.assertTrue(actual.toFile().exists());
            this.pathToTmpDir = fileUtil.getTempDirectory();
            actual.toFile().deleteOnExit();
        }

        @Test
        void prepFoldersForFile_WithDelete() throws IOException {
            Path actual = fileUtil.prepFoldersForFile(innerPath);

            Assertions.assertNotNull(actual);
            Assertions.assertTrue(actual.startsWith(fileUtil.getTempDirectory()));
            Assertions.assertTrue(actual.toFile().exists());

            fileUtil.tearDownRecursive(actual.toFile());

            Assertions.assertFalse(actual.toFile().exists());
            this.pathToTmpDir = fileUtil.getTempDirectory();
            actual.toFile().deleteOnExit();
        }

        @Test
        void prepFoldersForFile_Complex_WithDelete() throws IOException {
            Path actual = fileUtil.prepFoldersForFile(innerPath.concat("/%s".formatted("ABC")));

            Assertions.assertNotNull(actual);
            Assertions.assertTrue(actual.startsWith(fileUtil.getTempDirectory()));
            Assertions.assertTrue(actual.toFile().exists());

            fileUtil.tearDownRecursive(Path.of(fileUtil.getTempDirectory().toString(), innerPath).toFile());

            Assertions.assertFalse(actual.toFile().exists());
            this.pathToTmpDir = fileUtil.getTempDirectory();
            actual.toFile().deleteOnExit();
        }

        @Test
        void prepFoldersForFile_SuperComplex_WithDelete() throws IOException {
            Path actual = fileUtil.prepFoldersForFile(innerPath.concat("/%s/%s".formatted("ABC", "DEF")));

            Assertions.assertNotNull(actual);
            Assertions.assertTrue(actual.startsWith(fileUtil.getTempDirectory()));
            Assertions.assertTrue(actual.toFile().exists());

            fileUtil.tearDownRecursive(Path.of(fileUtil.getTempDirectory().toString(), innerPath).toFile());

            Assertions.assertFalse(actual.toFile().exists());
            this.pathToTmpDir = fileUtil.getTempDirectory();
            actual.toFile().deleteOnExit();
        }


        @Test
        void prepFoldersForFile_RuntimeException() {
            Assertions.assertThrows(RuntimeException.class, () -> fileUtil.prepFoldersForFile("*".repeat(10000)));
            this.pathToTmpDir = fileUtil.getTempDirectory();
        }

        @Test
        void prepFoldersForFile_IllegalArgFail() {
            try {
                fileUtil.prepFoldersForFile("*".repeat(10000));
            } catch (RuntimeException e) {
                Assertions.assertInstanceOf(FileSystemException.class, e.getCause());
            }
            this.pathToTmpDir = fileUtil.getTempDirectory();
        }

    }

    @Test
    void resolvePath() {
        Path actual, expected;
        expected = fileUtil.getTempDirectory();

        actual = fileUtil.resolvePath(fileUtil.getTempDirectory().toString());
        Assertions.assertEquals(expected, actual);
    }

    @Test
    void testCalcualteChecksum_Pass() throws IOException {
        File file = File.createTempFile(FileUtil.TMP_FILE_PREFIX, ".tmp", fileUtil.getTempDirectory().toFile());
        file.deleteOnExit();
        Long actual = fileUtil.calcualteChecksum(file);

        Assertions.assertNotNull(actual);
    }

    @Test
    void testCalcualteChecksum_Failure() {
        Long actual = fileUtil.calcualteChecksum(fileUtil.getTempDirectory().toFile());

        Assertions.assertNotNull(actual);
        Assertions.assertEquals(-1L, actual);
    }

    @Test
    void dataBufferUtilWrite() throws URISyntaxException {
        Path dataBufferSource = Path.of(this.getClass().getClassLoader().getResource("route_stop_order.txt").toURI());
        DataBufferFactory dataBufferFactory = new DefaultDataBufferFactory();
        Flux<DataBuffer> flux = DataBufferUtils.read(dataBufferSource, dataBufferFactory, 409600);

        Path destination = Path.of(this.fileUtil.getTempDirectory().toString(), "dataBufferTest");
        destination.toFile().deleteOnExit();

        Assertions.assertDoesNotThrow(() -> this.fileUtil.dataBufferUtilWrite(flux, destination));
        Assertions.assertTrue(destination.toFile().exists());
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
        log.info("AMTK-I-0000: Deleting [{}]", current.toPath());
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
        log.info("AMTK-I-0000: Deleting [{}]", current.toPath());
        return true;
    }
}