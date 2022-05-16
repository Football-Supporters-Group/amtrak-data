package com.wolginm.amtrak.data.models.gtfs;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Routes {
    
    private int route_id;
    private int agency_id;
    private String route_short_name;
    private String route_long_name;
    private String route_type;
    private String route_url;
    private String route_color;
    private String route_text_color;
}
