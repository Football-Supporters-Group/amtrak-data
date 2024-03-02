package com.markwolgin.amtrak.data.properties;

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

    private String schema = "https";
    /**
     * Base url for Amtrak data.
     */
    private String host = "//content.amtrak.com";
    /**
     * Context path for the GTFS url.  Incase Amtrak changes paths and I get
     *  lazy...
     */
    private String path = "/content/gtfs/GTFS.zip";
    /**
     * Longterm directory for inflated zipfiles.
     */
    private String dataDirectory = "data";

    private WebClientCustomProperties webClient;
    private RetryableProperties retry;

}
