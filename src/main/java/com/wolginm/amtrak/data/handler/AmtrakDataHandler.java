package com.wolginm.amtrak.data.handler;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.wolginm.amtrak.data.models.consolidated.ConsolidatedRoute;
import com.wolginm.amtrak.data.models.consolidated.ServiceDetails;
import com.wolginm.amtrak.data.models.consolidated.Trip;
import com.wolginm.amtrak.data.models.gtfs.Agency;
import com.wolginm.amtrak.data.models.gtfs.Routes;
import com.wolginm.amtrak.data.models.gtfs.Transfers;
import com.wolginm.amtrak.data.models.gtfs.Trips;
import com.wolginm.amtrak.data.properties.AmtrakProperties;
import com.wolginm.amtrak.data.util.AmtrakRestTemplate;
import com.wolginm.amtrak.data.util.CSVUtil;
import com.wolginm.amtrak.data.util.ZipUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class AmtrakDataHandler {
    
    private AmtrakProperties amtrakProperties;
    private AmtrakRestTemplate amtrakRestTemplate;
    private AmtrakCalendarHandler amtrakCalendarHandler;
    private AmtrakRoutesHandler amtrakRoutesHandler;
    private AmtrakStopTimesHandler amtrakStopTimesHandler;
    private CSVUtil csvUtil;
    private ZipUtil zipUtil;

    private List<Agency> agencies = new ArrayList<>();
    private List<Transfers> transfers = new ArrayList<>();
    private List<Trips> trips = new ArrayList<>();

    private List<Trip> compiledTrip = new ArrayList<>();
    private List<ConsolidatedRoute> consolidatedRoutes = new ArrayList<>();

    @Autowired
    public AmtrakDataHandler(AmtrakProperties amtrakProperties,
        AmtrakRestTemplate amtrakRestTemplate,
        AmtrakRoutesHandler amtrakRoutesHandler,
        AmtrakCalendarHandler amtrakCalendarHandler,
        AmtrakStopTimesHandler amtrakStopTimesHandler,
        CSVUtil csvUtil,
        ZipUtil zipUtil) {
        this.amtrakProperties = amtrakProperties;
        this.amtrakRestTemplate = amtrakRestTemplate;
        this.amtrakCalendarHandler = amtrakCalendarHandler;
        this.amtrakStopTimesHandler = amtrakStopTimesHandler;
        this.amtrakRoutesHandler = amtrakRoutesHandler;
        this.csvUtil = csvUtil;
        this.zipUtil = zipUtil;
    }

    /**
     * Will download the Amtrak GTFS.zip file to local for later use.
     * @param overrideChecksum  If true, it will expand regardless of the checksum status.
     * @return
     */
    public int updateAmtrakDataFiles(boolean overrideChecksum) {
        log.info("====== Update Amtrak Data Files ======");

        int status = 1;
        try {
            this.amtrakRestTemplate.downloadGTFSFile();
            this.zipUtil.unzip(this.amtrakProperties.getTempFile(), 
                this.amtrakProperties.getDataDirectory());        

            status = 0;
        } catch (IOException e) {
            log.error("Failed to download Amtrak Data");
            e.printStackTrace();
        }

        log.info("====== End Amtrak Data Files ======");
        return status;
    }

    public int loadFilesToMemory() {
        int status = 0;

		try {
            ;
            this.agencies = csvUtil.csvToObject(
                new FileInputStream(
                    new File(
                        String.format("%s/%s", this.amtrakProperties.getDataDirectory(), "agency.txt"))), new Agency())
                .stream().map((entity) -> {
                    return (Agency) entity;
                }).collect(Collectors.toList());
            log.info("Loaded {} agencies", this.agencies.size());

            this.amtrakCalendarHandler.loadRoutesFromFiles();

            this.amtrakRoutesHandler.loadRoutesFromFiles();

            this.amtrakStopTimesHandler.loadRoutesFromFiles();

            this.transfers = csvUtil.csvToObject(new FileInputStream(new File(
                String.format("%s/%s", this.amtrakProperties.getDataDirectory(), "transfers.txt"))), new Transfers())
                .stream().map((entity) -> {
                    return (Transfers) entity;
                }).collect(Collectors.toList());;
            log.info("Loaded {} transfers", this.transfers.size());

            this.trips = csvUtil.csvToObject(new FileInputStream(new File(
                String.format("%s/%s", this.amtrakProperties.getDataDirectory(), "trips.txt"))), new Trips())
                .stream().map((entity) -> {
                    return (Trips) entity;
                }).collect(Collectors.toList());;
            log.info("Loaded {} trips", this.trips.size());
        } catch (FileNotFoundException e) {
            log.error("Unable to load all data!");
            e.printStackTrace();
        }
    
        return status;
    }

    public int compileTrips() {
        int status = 0;
        Trip trip;

        for (Trips selectedTrip : this.trips) {
            // trip = new Trip(this.amtrakRoutesHandler.findRouteByRoutesId(selectedTrip.getRoute_id()), 
            //     new ServiceDetails(this.amtrakCalendarHandler.findRouteByServiceId(selectedTrip.getService_id())));
            trip = new Trip(selectedTrip.getRoute_id(), 
                new ServiceDetails(this.amtrakCalendarHandler.findRouteByServiceId(selectedTrip.getService_id())));
            trip.addAllScheduleStop(this.amtrakStopTimesHandler.findAllStopsByTripId(selectedTrip.getTrip_id()));

            this.compiledTrip.add(trip);
        }

        log.info("Compiled details for {} trips", compiledTrip.size());
        return status;
    }
    
    public int compileRoutes() {
        int status = 0;

        for (Routes selectedRoute : this.amtrakRoutesHandler.getAllRoutes()) {
            consolidatedRoutes.add(new ConsolidatedRoute(
                this.getTripsByRouteId(selectedRoute.getRoute_id()), selectedRoute));
        }

        log.info("Compiled details for {} routes", consolidatedRoutes.size());
        return status;
    }

    private List<Trip> getTripsByRouteId(int routeId) {
        return this.compiledTrip
            .stream()
            .filter((trip) -> trip.getRouteId() == routeId)
            .collect(Collectors.toList());
    }

    public List<Trip> getTrips() {
        return this.compiledTrip;
    }

    public List<ConsolidatedRoute> getRoutes() {
        return this.consolidatedRoutes;
    }
}
