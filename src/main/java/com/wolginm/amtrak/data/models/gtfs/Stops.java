package com.wolginm.amtrak.data.models.gtfs;

import java.util.List;
import java.util.TimeZone;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
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
}
