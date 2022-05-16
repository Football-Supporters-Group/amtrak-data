package com.wolginm.amtrak.data.models.gtfs;

import java.sql.Time;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Trips {
    
    private int route_id;
    private String service_id;
    private String trip_id;
    private String trip_headsign;
    private String trip_short_name;
    private int direction_id;
    private int shape_id;

    /**
     * In mins
     */
    private int min_transfer_time;
}
