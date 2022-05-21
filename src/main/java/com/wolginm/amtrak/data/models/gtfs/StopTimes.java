package com.wolginm.amtrak.data.models.gtfs;

import java.sql.Time;
import java.util.List;

import com.wolginm.amtrak.data.util.GTFSUtil;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StopTimes implements ICVMapable {
    
    private String trip_id;
    private Time arrival_time;
    private Time departure_time;
    private String stop_id;
    private int stop_sequence;
    private int pickup_time;
    private int drop_off_type;

    @Override
    public ICVMapable mapToObject(List<Object> objectList, List<String> headersList) {
        StopTimes stopTimes = new StopTimes();
        stopTimes.setTrip_id((String) objectList.get(0));
        stopTimes.setArrival_time(GTFSUtil.parseTime(objectList.get(1)));
        stopTimes.setDeparture_time(GTFSUtil.parseTime(objectList.get(2)));
        stopTimes.setStop_id((String) objectList.get(3));
        stopTimes.setStop_sequence(Integer.parseInt((String) objectList.get(4)));
        stopTimes.setPickup_time(Integer.parseInt((String) objectList.get(5)));
        stopTimes.setDrop_off_type(Integer.parseInt((String) objectList.get(6)));
        return stopTimes;
    }
}
