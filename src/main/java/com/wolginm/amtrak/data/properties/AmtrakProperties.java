package com.wolginm.amtrak.data.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;


@Data
@Configuration
@EnableConfigurationProperties
@ConfigurationProperties("amtrak")
public class AmtrakProperties {
    
    private String gtfsUrl;
    private String gtfsUri;
    private String tempFile = "./tmp";
    private String dataDirectory;
    private String routeMetadata;
}
