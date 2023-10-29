package com.wolginm.amtrak.data.models.gtfs;

import java.sql.Time;
import java.util.List;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

/**
 * A Trip is an instance of a route, known as a service.  The service is defined by
 *  a calendar entry.  So a trip can be though of a specific train, like the
 *  Pennsylvania 42/43, or the Keystone 661.
 */
@Data
public class Trips implements ICVMapable {
    
    private int route_id;
    private String service_id;
    private String trip_id;
    private String trip_headsign;
    private String trip_short_name;
    private int direction_id;
    private int shape_id;

    /**
     * In mins
     */
    private int min_transfer_time;

    @Override
    public ICVMapable mapToObject(List<Object> objectList, List<String> headersList) {
        Trips trips = new Trips();
        int shapeId = ((String) objectList.get(6)).length() > 0 ? Integer.parseInt((String) objectList.get(6)) : 0;
        trips.setRoute_id(Integer.parseInt((String) objectList.get(0)));
        trips.setService_id((String) objectList.get(1));
        trips.setTrip_id((String) objectList.get(2));
        trips.setTrip_headsign((String) objectList.get(3));
        trips.setTrip_short_name((String) objectList.get(4));
        trips.setDirection_id(Integer.parseInt((String) objectList.get(5)));
        trips.setShape_id(shapeId);

        return trips;
    }
}
