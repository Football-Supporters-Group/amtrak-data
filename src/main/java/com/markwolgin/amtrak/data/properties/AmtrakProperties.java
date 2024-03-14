package com.markwolgin.amtrak.data.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.Objects;

/**
 * Amtrak Data Properties.
 */
@Data
@Configuration
@EnableConfigurationProperties
@ConfigurationProperties("amtrak")
public class AmtrakProperties {

    /**
     * Metadata the contains a master list of route ordering.  Its very important,
     *  so our ordered lists are correct.
     */
    private String route_metadata = "metadata/route_stop_order.txt";

    /**
     * Amtrak Gtfs Properties, for indicating where to get and put the 
     *  Gtfs files.
     */
    private GtfsProperties gtfs;
}
