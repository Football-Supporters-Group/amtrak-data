package com.wolginm.amtrak.data.models.consolidated;

import java.util.ArrayList;
import java.util.List;

import com.wolginm.amtrak.data.models.gtfs.Routes;
import com.wolginm.amtrak.data.models.gtfs.Stops;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ConsolidatedRoute {

    private List<Trip> trips;
    private List<String> stopIds;
    private Routes route;

    public ConsolidatedRoute(List<Trip> trips,
        Routes route) {
        this.trips = trips;
        this.route = route;
        this.stopIds = this.getAllStopIds();
    }

    public List<String> getAllStopIds() {
        List<String> stops = new ArrayList<>();
        for (Trip trip : trips) {
            trip.getSchedule().forEachRemaining((stop) -> {
                String temp = null;
                temp = stop.getStop().getStop_id();
                if (!stops.contains(temp)) stops.add(temp);
            });
        }
        
        return stops;
    }

    @Override
    public String toString() {
        return String.format("%s - %d scheduled trips", this.route.getRoute_long_name(), this.trips.size());
    }
    
}
