package com.wolginm.amtrak.data.handler;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.wolginm.amtrak.data.models.consolidated.Stop;
import com.wolginm.amtrak.data.models.gtfs.StopTimes;
import com.wolginm.amtrak.data.models.gtfs.Stops;
import com.wolginm.amtrak.data.properties.AmtrakProperties;
import com.wolginm.amtrak.data.util.CSVUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class AmtrakStopTimesHandler {
    
    
    private CSVUtil csvUtil;
    private AmtrakProperties amtrakProperties;

    private List<Stops> stops = new ArrayList<>();
    private List<StopTimes> stop_times = new ArrayList<>();

    @Autowired
    public AmtrakStopTimesHandler(CSVUtil csvUtil,
        AmtrakProperties amtrakProperties) {
        this.csvUtil = csvUtil;
        this.amtrakProperties = amtrakProperties;
    }

    public void loadRoutesFromFiles() throws FileNotFoundException {
        this.stops = csvUtil.csvToObject(new FileInputStream(new File(
            String.format("%s/%s", this.amtrakProperties.getDataDirectory(), "stops.txt"))), new Stops())
            .stream().map((entity) -> {
                return (Stops) entity;
            }).collect(Collectors.toList());;
        log.info("Loaded {} stops", this.stops.size());

        this.stop_times = csvUtil.csvToObject(new FileInputStream(new File(
            String.format("%s/%s", this.amtrakProperties.getDataDirectory(), "stop_times.txt"))), new StopTimes())
            .stream().map((entity) -> {
                return (StopTimes) entity;
            }).collect(Collectors.toList());;
        log.info("Loaded {} Time Table Entries", this.stop_times.size());
    }

    public List<StopTimes> findAllStopTimesByTripId(String tripId) {
        List<StopTimes> stopTimes = this.stop_times
            .stream()
            .filter((element) -> element.getTrip_id().equals(tripId))
            .collect(Collectors.toList());
        if (stopTimes.size() < 1) {
            log.error("Unable to find specific time table entry {}!", tripId);
        }

        return stopTimes;
    }

    public Stops findStopTimesByStopId(String stopId) {
        List<Stops> stop = this.stops
            .stream()
            .filter((element) -> element.getStop_id().equals(stopId))
            .collect(Collectors.toList());
        if (stop.size() > 1) {
            log.error("Unable to find specific stop {}!", stopId);
        }

        return stop.get(0);
    }


    public List<Stop> findAllStopsByTripId(String tripId) {
        List<StopTimes> stopTimes = this.findAllStopTimesByTripId(tripId);

        List<Stop> stop_list = new ArrayList<>();
        Stops stops = null;
        for (StopTimes stopTimeEntry : stopTimes) {
            stops = this.findStopTimesByStopId(stopTimeEntry.getStop_id());
            stop_list.add(new Stop(stops, stopTimeEntry));
        }

        if (stop_list.size() < 1) log.error("Unable to build up Stop for trip ID {}!", tripId);
        return stop_list;
    }

}
