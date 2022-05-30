package com.wolginm.amtrak.data.models.consolidated;

import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import com.wolginm.amtrak.data.models.gtfs.Routes;
import com.wolginm.amtrak.data.models.gtfs.Stops;

public class Trip {
    
    private int routeId;
    private ServiceDetails serviceDetails;
    private String tripId;
    
    private Set<Stop> stops;

    public Trip(final int routeId,
        final ServiceDetails serviceDetails,
        final String tripId) {
            this.routeId = routeId;
            this.serviceDetails = serviceDetails;
            this.tripId = tripId;
            this.stops = new TreeSet<>();
    }

    public int addScheduleStop(final Stop stop) {
        this.stops.add(stop);
        return this.stops.size();
    }

    public int addAllScheduleStop(final List<Stop> stop) {
        this.stops.addAll(stop);
        return this.stops.size();
    }

    public int getRouteId() {
        return this.routeId;
    }

    public String getTripId() {
        return tripId;
    }

    public ServiceDetails getServiceDetails() {
        return this.serviceDetails;
    }

    public Iterator<Stop> getSchedule() {
        return this.stops.iterator();
    }

    @Override
    public String toString() {
        Stop first = this.getFirstElement(this.stops);
        Stop last = this.getLastElement(this.stops);
        return String.format("%s to %s Dpt: %s -> Arv: %s", 
            first.getStop().getStop_id(), 
            last.getStop().getStop_id(), 
            first.getStopTimes().getDeparture_time(), 
            last.getStopTimes().getArrival_time());
    }

    private <T> T getFirstElement(final Iterable<T> elements) {
        return elements.iterator().next();
    }
    
    
    private <T> T getLastElement(final Iterable<T> elements) {
        T lastElement = null;
    
        for (T element : elements) {
            lastElement = element;
        }
    
        return lastElement;
    }
}
