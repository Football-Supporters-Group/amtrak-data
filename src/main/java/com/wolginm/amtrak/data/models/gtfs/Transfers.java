package com.wolginm.amtrak.data.models.gtfs;

import java.sql.Time;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Transfers {
    
    private String from_stop_id;
    private String to_stop_id;
    private int transfer_type;

    /**
     * In mins
     */
    private int min_transfer_time;
}
