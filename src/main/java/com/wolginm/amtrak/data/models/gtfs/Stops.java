package com.wolginm.amtrak.data.models.gtfs;

import java.util.List;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

/**
 * Each stop accessible by the Amtrak network.  Example, 30th Street Station.
 */
@Data
public class Stops implements ICVMapable {
    
    private String stop_id;
    private String stop_name;
    private double stop_lat;
    private double stop_lon;
    private String stop_url;
    private String stop_timezone;

    @Override
    public ICVMapable mapToObject(List<Object> objectList, List<String> headersList) {
        Stops stops = new Stops();
        stops.setStop_id((String) objectList.get(0));
        stops.setStop_name((String) objectList.get(1));
        stops.setStop_lat(Double.parseDouble((String) objectList.get(2)));
        stops.setStop_lon(Double.parseDouble((String) objectList.get(3)));
        stops.setStop_url((String) objectList.get(4));
        stops.setStop_timezone((String) objectList.get(5));
        return stops;
    }

    @Override
    public boolean equals(Object o) {
        Stops other = (Stops) o;
        return this.stop_id.equals(other.getStop_id())
            && this.stop_name.equals(other.getStop_name())
            && this.stop_lat == other.getStop_lat()
            && this.stop_lon == other.getStop_lon()
            && this.stop_url.equals(other.getStop_url())
            && this.stop_timezone.equals(other.getStop_timezone());
    }
}
