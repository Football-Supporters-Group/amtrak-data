package com.wolginm.amtrak.data.handler;

import com.wolginm.amtrak.data.models.gtfs.ICVMapable;
import com.wolginm.amtrak.data.models.gtfs.StopTimes;
import com.wolginm.amtrak.data.models.gtfs.Stops;
import com.wolginm.amtrak.data.properties.AmtrakProperties;
import com.wolginm.amtrak.data.util.CSVUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Will handle the prep-work for the handler for all Stops.
 * This will take some amount of work, and staging that is
 * required or there will be some duplicate stuff happening.
 */
@Slf4j
@Component
@DependsOn("AmtrakDataService")
public class AmtrakStopHandler {

    /**
     * A list of all stops in the Amtrak Network.
     */
    private final List<Stops> stops;
    /**
     * A map of trip ids to list of trip StopTimes.
     * Can get stops via this handler.
     */
    private final Map<Integer, List<StopTimes>> tripStopTimeMap;
    /**
     * A map of route ids to list of stops in the route.
     * Data comes from the route_order_metadata.txt.
     */
    private final Map<Integer, List<Stops>> routeStopMap;
    /**
     * Amtrak Properties file.
     */
    private final AmtrakProperties amtrakProperties;
    /**
     * CSV utility for reading and processing the GTFS data.
     */
    private final CSVUtil csvUtil;

    /**
     * Default constructor for the Amtrak Stop Handler.
     * @param amtrakProperties  Amtrak Properties File
     * @param csvUtil           CSV utility.
     */
    public AmtrakStopHandler(final AmtrakProperties amtrakProperties,
                             final CSVUtil csvUtil) {
        this.amtrakProperties = amtrakProperties;
        this.csvUtil = csvUtil;

        this.stops = this.loadAllStops(this.amtrakProperties.getDataDirectory());
        this.tripStopTimeMap = this.loadStopTimesByTripId(this.amtrakProperties.getDataDirectory());
        this.routeStopMap = this.loadRouteStops(this.amtrakProperties.getRouteMetadata());
    }

    /**
     * Loads all {@link Stops} into the list of all stops.
     * @param dataDirectory The location of the data for processing.
     * @return              The list of all {@link Stops}.
     */
    protected List<Stops> loadAllStops(final String dataDirectory) {
        List<Stops> stops = null;
        try {
            stops = csvUtil.csvToObject(new FileInputStream(new File(
                            String.format("%s/%s", dataDirectory, "stops.txt"))), new Stops())
                    .stream().map((entity) -> {
                        return (Stops) entity;
                    }).collect(Collectors.toList());

            log.info("Loaded {} stops", stops.size());
        } catch (FileNotFoundException e) {
            log.error("Uanable to load the required Stops data!");
            throw new RuntimeException(e);
        }

        return stops;
    }

    /**
     * Loads all {@link StopTimes} into the map of trips to stops.
     * @param dataDirectory THe location of the data for processing.
     * @return              The map of all {@link StopTimes}.
     */
    protected Map<Integer, List<StopTimes>> loadStopTimesByTripId(final String dataDirectory) {
        Map<Integer, List<StopTimes>> stopTimesByTripId = new HashMap<>();
        try {
            StopTimes tmpStopTimes  = null;
            List<StopTimes> stopTimes;
            for (ICVMapable swap : this.csvUtil.csvToObject(new FileInputStream(new File(
                    String.format("%s/%s", dataDirectory, "stop_times.txt"))),
                    new StopTimes())) {
                tmpStopTimes = (StopTimes) swap;

                stopTimes = stopTimesByTripId.get(tmpStopTimes.getTrip_id());
                if (null == stopTimes) {
                    stopTimes = new LinkedList<>();
                    stopTimesByTripId.put(Integer.valueOf(tmpStopTimes.getTrip_id()), stopTimes);
                }

                stopTimes.add(tmpStopTimes);
            }
            log.info("Loaded {} stops times routes", stopTimesByTripId.size());

        } catch (FileNotFoundException e) {
            log.error("Unable to load the required StopTimes data!");
            throw new RuntimeException(e);
        }

        return stopTimesByTripId;
    }

    /**
     * Will attempt to load the Route MetaData from the route_stop_order.txt file.
     *  This is required, as without we are not able to correclty handle routes
     *  with skip-stop-patterns.  The Northeast Regional being the biggest offender.
     * @param metaDataDirectory Location of the metadata file.
     * @return                  The loaded routes, or an empty map.
     */
    protected Map<Integer, List<Stops>> loadRouteStops(final String metaDataDirectory) {
        Map<Integer, List<String>> stopByRouteIdString;
        Map<Integer, List<Stops>> stopByRouteIdStops;

        try {
            stopByRouteIdString = csvUtil.csvToRouteOrderMap(
                    new FileInputStream(new File(
                            String.format("%s/%s", metaDataDirectory, "route_stop_order.txt"))));

            stopByRouteIdStops = new HashMap<>();
            for (Map.Entry<Integer, List<String>> route : stopByRouteIdString.entrySet()) {
                List<Stops> stops = new LinkedList<>();

                for (String stopId : route.getValue()) {
                    stops.add(this.getStopFromStopId(stopId));
                }

                stopByRouteIdStops.put(route.getKey(), stops);
            }
        } catch (FileNotFoundException e) {
            log.error("Failed to load route metadata! {}", e.getMessage());
            log.error("Defaulting routesToOrderedList to empty map!");
            log.error("This will cause multiple issues if you require accurate station placement in a list.");
            stopByRouteIdStops = new HashMap<>();
            e.printStackTrace();
        }

        return stopByRouteIdStops;
    }

    /**
     * Will get the {@link Stops} based on a supplied stop_id.
     * @param stopId    The index of the {@link Stops}.
     * @return          The selected {@link Stops} if it exists.
     *  If it does not, it will return a null.
     */
    public Stops getStopFromStopId(final String stopId) {
        Stops selectedStop;
        List<Stops> selected = this.stops.stream().filter((stp) -> {
            return stp.getStop_id().equals(stopId);
        }).collect(Collectors.toList());

        if (selected.size() != 1) {
            selectedStop = selected.get(0);
        } else {
            selectedStop = null;
        }
        return selectedStop;
    }

    /**
     * Will get the {@link Stops} based on a supplied stop_id.
     * @param stopIds   A list of stop ids for getting.
     * @return          The selected {@link Stops} if it exists.
     *  If it does not, it will return a null.
     */
    public List<Stops> getStopFromStopId(final String... stopIds) {
        List<Stops> converted = new LinkedList<>();

        for (String stopId: stopIds) {
            converted.add(this.getStopFromStopId(stopId));
        }

        return converted;
    }

    /**
     * Will get the list of the {@link StopTimes} for a specific tripId.
     * @param tripId    The trip id to pull.
     * @return          The list of stop times for a trip, or null of the
     *  trip id is not valid.
     */
    public List<StopTimes> getStopTimesForTrip(final Integer tripId) {
        return this.tripStopTimeMap.get(tripId);
    }

}
