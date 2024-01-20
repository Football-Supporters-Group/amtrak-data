package com.wolginm.amtrak.data.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Data;

/**
 * Amtrak Gtfs Properties, for indicating where to get and put the 
 *  Gtfs files.
 */
@Data
@Configuration
@EnableConfigurationProperties
@ConfigurationProperties("amtrak.gtfs")
public class GtfsProperties {
    
    /**
     * Base url for Amtrak data.
     */
    private String content_amtrak = "https://content.amtrak.com/";
    /**
     * Context path for the GTFS url.  Incase Amtrak changes paths and I get
     *  lazy...
     */
    private String uri_part = "content/gtfs/GTFS.zip";
    /**
     * Temp directory to put zip file, so simple deletion.
     */
    private String temp_file = "./tmp";
    /**
     * Longterm directory for inflated zipfiles.
     */
    private String data_directory = "./data";

}
