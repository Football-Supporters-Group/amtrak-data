package com.wolginm.amtrak.data.models.gtfs;

import java.util.TimeZone;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Stops {
    
    private String stop_id;
    private String stop_name;
    private double stop_lat;
    private double stop_lon;
    private String stop_url;
    private TimeZone stop_timezone;
}
