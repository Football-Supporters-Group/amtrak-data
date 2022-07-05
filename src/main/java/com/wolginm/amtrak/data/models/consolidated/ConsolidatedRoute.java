package com.wolginm.amtrak.data.models.consolidated;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.wolginm.amtrak.data.models.gtfs.Routes;
import com.wolginm.amtrak.data.models.gtfs.Stops;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Getter
@Setter
@Slf4j
public class ConsolidatedRoute {

    private List<Trip> trips;
    private List<String> stopIds;
    private Map<Integer, Integer> maxNumberOfStationPerDirection;
    private Routes route;

    public ConsolidatedRoute(List<Trip> trips,
        Routes route) {
        this.trips = trips;
        this.route = route;
        this.setupStopsAndDirectionality();
    }

    public void setupStopsAndDirectionality() {
        List<String> stops = new ArrayList<>();
        this.maxNumberOfStationPerDirection = new HashMap<>();

        String temp = null;
        Stop stop;
        Integer count, currentMax;
        Iterator<Stop> iterator;
        for (Trip trip : trips) {
            
            count = 0;
            iterator = trip.getSchedule();
            while (iterator.hasNext()) {
                count ++;
                stop = iterator.next();
                temp = stop.getStop().getStop_id();
                if (!stops.contains(temp)) stops.add(temp);
            }

            currentMax = this.maxNumberOfStationPerDirection.get(trip.getDirectionId());
            if (currentMax == null) this.maxNumberOfStationPerDirection.put(trip.getDirectionId(), count);
            else if (count > currentMax) this.maxNumberOfStationPerDirection.put(trip.getDirectionId(), count);
        }

        this.stopIds =  stops;
    }

    @Override
    public String toString() {
        return String.format("%s - %d scheduled trips", this.route.getRoute_long_name(), this.trips.size());
    }
    
}
