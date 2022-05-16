package com.wolginm.amtrak.data.models.consolidated;

import java.util.Comparator;

import com.wolginm.amtrak.data.models.gtfs.StopTimes;
import com.wolginm.amtrak.data.models.gtfs.Stops;

import lombok.Getter;

@Getter
public class Stop implements Comparator {
    private Stops stop;
    private StopTimes stopTimes;
    
    public Stop(final Stops stop, 
        final StopTimes stopTimes) {
        this.stop = stop;
        this.stopTimes = stopTimes;
    }

    @Override
    public int compare(Object arg0, Object arg1) {
        Stop a = (Stop) arg0;
        Stop b = (Stop) arg1;

        int result = a.stopTimes.getStop_sequence()
            - b.stopTimes.getStop_sequence();

        return result;
    }
}

