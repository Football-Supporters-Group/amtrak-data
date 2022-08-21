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
import lombok.extern.slf4j.Slf4j;

@Getter
@Setter
@Configuration
// @EnableConfigurationProperties
// @ConfigurationProperties()
public class AmtrakProperties {
    
    @Value("${amtrak.gtfs-url}") 
    private String gtfsUrl;

    @Value("${amtrak.gtfs-uri}") 
    private String gtfsUri;

    @Value("${amtrak.temp-file}") 
    private String tempFile;

    @Value("${amtrak.data-directory}") 
    private String dataDirectory;

    @Value("${amtrak.route-metadata}")
    private String routeMetadata;

    @Value(value = "${amtrak.data-update-ms}")
    private String dataUpdateMs;

}
