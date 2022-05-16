package com.wolginm.amtrak.data.models.consolidated;

import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

public class Trip {
    
    private RouteDetails routeDetails;
    private ServiceDetails serviceDetails;
    
    private Set<Stop> stops;

    public Trip(final RouteDetails routeDetails,
        final ServiceDetails serviceDetails) {
            this.routeDetails = routeDetails;
            this.serviceDetails = serviceDetails;
            this.stops = new TreeSet<>();
    }

    public int addScheduleStop(final Stop stop) {
        this.stops.add(stop);
        return this.stops.size();
    }

    public RouteDetails getRouteDetails() {
        return this.routeDetails;
    }

    public ServiceDetails getServiceDetails() {
        return this.serviceDetails;
    }

    public Iterator<Stop> getSchedule() {
        return this.stops.iterator();
    }
    
}
