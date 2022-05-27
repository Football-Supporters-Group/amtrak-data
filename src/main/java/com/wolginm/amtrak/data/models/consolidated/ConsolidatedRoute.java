package com.wolginm.amtrak.data.models.consolidated;

import java.util.List;

import com.wolginm.amtrak.data.models.gtfs.Routes;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ConsolidatedRoute {

    private List<Trip> trips;
    private Routes route;

    public ConsolidatedRoute(List<Trip> trips,
        Routes route) {
        this.trips = trips;
        this.route = route;
    }

    @Override
    public String toString() {
        return String.format("%s - %d scheduled trips", this.route.getRoute_long_name(), this.trips.size());
    }
    
}
