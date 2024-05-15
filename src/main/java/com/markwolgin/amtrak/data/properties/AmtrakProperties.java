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
     * Metadata that contains a master list of every routes most common start station per direction.
     *  This is very important, as it provides a way for us to draw the schedules in a
     *  coherent way.  Otherwise, we can get "time ordered" stations that actually arrive out of order later down the schedule.
     */
    private String default_station_order = "metadata/route_default_station.txt";

    /**
     * Amtrak Gtfs Properties, for indicating where to get and put the 
     *  Gtfs files.
     */
    private GtfsProperties gtfs;
}
