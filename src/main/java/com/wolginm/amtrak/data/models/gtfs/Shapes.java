package com.wolginm.amtrak.data.models.gtfs;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Shapes {
    
    private int shape_id;
    private double shape_pt_lat;
    private double shape_pt_lon;
    private int shape_pt_sequence;
}
