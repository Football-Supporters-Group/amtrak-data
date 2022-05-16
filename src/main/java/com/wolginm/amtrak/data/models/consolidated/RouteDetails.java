package com.wolginm.amtrak.data.models.consolidated;

import com.wolginm.amtrak.data.models.gtfs.Routes;

import lombok.Getter;

@Getter
public class RouteDetails {
    
    private int route_id;
    private int agency_id;
    private String route_short_name;
    private String route_long_name;
    private String route_url;

    public RouteDetails(final Routes route) {
        this.route_id = route.getRoute_id();
        this.agency_id = route.getAgency_id();
        this.route_short_name = route.getRoute_short_name();
        this.route_long_name = route.getRoute_long_name();
        this.route_url = route.getRoute_url();
    }
}
