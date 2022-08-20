package com.wolginm.amtrak.data.properties;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.wolginm.amtrak.data.util.FileUtil;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Configuration
@EnableConfigurationProperties
@ConfigurationProperties("amtrak")
public class AmtrakProperties {
    
    @Value("amtrak.gtfs-url") 
    private String gtfsUrl;

    @Value("amtrak.gtfs-uri") 
    private String gtfsUri;

    @Value("amtrak.temp-file") 
    private String tempFile;

    @Value("amtrak.data-directory") 
    private String dataDirectory;

    @Value("amtrak.route-metadata")
    private String routeMetadata;

    @Value("amtrak.data-update-ms")
    private Integer dataUpdateMs;

    @Bean
    @Qualifier("NeedToPullData")
    public boolean needToPullData(FileUtil fileUtil) throws IOException {
        Path dataDirectory = fileUtil.resolvePath(this.dataDirectory);
        return fileUtil.directoryExists(dataDirectory)
            && fileUtil
                .compareAgeToConstantTime(fileUtil.getAgeInMili(dataDirectory), this.dataUpdateMs) < 0 ;       
    }
}
