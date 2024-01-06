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
    
    private String content_amtrak = "https://content.amtrak.com/";
    private String gtfs_uri_part = "content/gtfs/GTFS.zip";
    private String temp_file = "./tmp";
    private String data_directory = "./data";
    private String route_metadata = "./src/main/resources/metadata/route_stop_order.txt";
}
