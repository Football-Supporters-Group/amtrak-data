package com.wolginm.amtrak.data.configuration;

import com.wolginm.amtrak.data.util.FileUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Slf4j
@Configuration
public class TemporaryDirectoryConfiguration {

    private Path temporaryDirectory;
    public final static String TMP_FILE_PREFIX = "amtk_data";


    @Bean("temporaryDirectoryPath")
    public Path getTemporaryDirectory() throws IOException {
        temporaryDirectory = Files.createTempDirectory(TMP_FILE_PREFIX);
        temporaryDirectory.toFile().deleteOnExit();
        log.info("AMTK-5300: Created temporary directory for local file storage.  " +
                "Directory has been marked for termination on close of JVM.  " +
                "Directory location is [{}]", temporaryDirectory.toString());
        return temporaryDirectory;
    }
}
