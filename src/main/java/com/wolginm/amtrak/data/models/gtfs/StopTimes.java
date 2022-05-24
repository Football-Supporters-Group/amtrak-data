package com.wolginm.amtrak.data.models.gtfs;

import java.time.LocalTime;
import java.util.List;

import com.wolginm.amtrak.data.util.GTFSUtil;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StopTimes implements ICVMapable {
    
    private String trip_id;
    private LocalTime arrival_time;
    private LocalTime departure_time;
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

    @Override
    public boolean equals(Object o) {
        StopTimes other = (StopTimes) o;
        return this.trip_id.equals(other.getTrip_id())
            && this.arrival_time.equals(other.getArrival_time())
            && this.departure_time.equals(other.getDeparture_time())
            && this.stop_id.equals(other.getStop_id())
            && this.stop_sequence == other.getStop_sequence()
            && this.pickup_time == other.getPickup_time()
            && this.drop_off_type == other.getDrop_off_type();
    }
}
