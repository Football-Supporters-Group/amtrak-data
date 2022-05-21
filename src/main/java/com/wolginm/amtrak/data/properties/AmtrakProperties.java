package com.wolginm.amtrak.data.properties;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

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
}
