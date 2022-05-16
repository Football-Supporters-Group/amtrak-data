package com.wolginm.amtrak.data.models.gtfs;

import java.sql.Time;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StopTimes {
    
    private String trip_id;
    private Time arrival_time;
    private Time departure_time;
    private String stop_id;
    private int stop_sequence;
    private int pickup_time;
    private int drop_off_type;
}
