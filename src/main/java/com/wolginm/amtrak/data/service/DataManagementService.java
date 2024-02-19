package com.wolginm.amtrak.data.service;

import com.wolginm.amtrak.data.client.AmtrakDataClient;
import com.wolginm.amtrak.data.properties.AmtrakProperties;
import com.wolginm.amtrak.data.util.DataMappingUtil;
import com.wolginm.amtrak.data.util.ZipUtil;
import com.wolginmark.amtrak.data.models.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.NotDirectoryException;
import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class DataManagementService {

    private final InflationService inflationService;
    private final AmtrakDataClient amtrakDataClient;
    private final AmtrakProperties amtrakProperties;
    private final DataMappingUtil dataMappingUtil;
    private final Path temporaryDirectory;
    private final ZipUtil zipUtil;

    private Map<Integer, ConsolidatedRoute> mapOfRoutes;

    private Instant lastRefresh;

    public DataManagementService(final InflationService inflationService,
                                 final AmtrakDataClient amtrakDataClient,
                                 final ZipUtil zipUtil,
                                 final DataMappingUtil dataMappingUtil,
                                 final AmtrakProperties amtrakProperties,
                                 @Qualifier("temporaryDirectoryPath") final Path tempDir) {
        this.inflationService = inflationService;
        this.amtrakDataClient = amtrakDataClient;
        this.amtrakProperties = amtrakProperties;
        this.dataMappingUtil = dataMappingUtil;
        this.temporaryDirectory = tempDir;
        this.zipUtil = zipUtil;
    }

    public ConsolidatedResponseObject buildConsolidatedResponseObject(String... routesToLoad) {
        log.info("AMTK-2210: In buildConsolidatedResponseObject, will start processing request for routes: [{}]", List.of(routesToLoad));
        if (mapOfRoutes == null || mapOfRoutes.isEmpty()) {
            this.mapOfRoutes = this.loadAmtrakDataIntoLocal();
            if (mapOfRoutes == null || mapOfRoutes.isEmpty()) {
                String error = "AMTK-2299: Unable to load in Amtrak Data!  All further responses will be invalid until corrected.";
                log.error(error);
                this.mapOfRoutes = Map.of();
            }
        }
        ConsolidatedResponseObject responseObject = new ConsolidatedResponseObject();
        responseObject.setLastTimeDataWasRefreshed(lastRefresh.toString());
        responseObject.setTimestamp(Instant.now().toString());
        responseObject.setRequestedRouteIds(this.generateListOfRequestedRouteIds(routesToLoad));



        return responseObject;
    }

    /**
     * Gets the timestamp of when data was last refreshed from the server.
     * @return  The last time the data was refreshed.
     */
    public Instant getLastTimeLastRefresh() {
        return this.lastRefresh;
    }

    /**
     * Gets the amount of time that has passed since the last data refresh.
     * @return  Amount of time that has passed since the last data refresh
     */
    public Duration getLengthOfTimeSinceLastRefresh() {
        Duration difference = Duration.between(lastRefresh, Instant.now());
        return difference;
    }

    private <T extends AmtrakObject> Map<Integer, ConsolidatedRoute> loadAmtrakDataIntoLocal() {
        Map<Integer, ConsolidatedRoute> newRoutes = null;
        try {
            this.refreshAmtrakData();

            Map<Class<T>, List<T>> allAmtrakObjects = this.inflationService.inflateAllAmtrakObjects();
            Map<Integer, LinkedHashSet<String>> routeMetaData = this.inflationService.inflateRouteOrderMetadata();

            //StopTimes, Calendars(Nullable), Trips, Shapes(Nullable)
            Map<Integer, ConsolidatedTrip> consolidatedTripIntermediate
                    = this.dataMappingUtil.buildConsolidatedTripMap((List<StopTimes>) allAmtrakObjects.get(StopTimes.class),
                    (List<Calendar>) allAmtrakObjects.get(Calendar.class),
                    (List<Trips>) allAmtrakObjects.get(Trips.class),
                    (List<Shapes>) allAmtrakObjects.get(Shapes.class));

            //Trips, ConsolidatedTripMap, Routes, Calendars, Stops
            newRoutes = this.dataMappingUtil.buildConsolidatedRouteMap((List<Trips>) allAmtrakObjects.get(Trips.class),
                    consolidatedTripIntermediate,
                    (List<Routes>) allAmtrakObjects.get(Routes.class),
                    (List<Calendar>) allAmtrakObjects.get(Shapes.class),
                    this.dataMappingUtil.stopsMap((List<Stops>) allAmtrakObjects.get(Stops.class)),
                    routeMetaData);

            this.lastRefresh = Instant.now();
        } catch (NotDirectoryException | FileNotFoundException e) {
            log.error("AMTK-2299: Unable to load Amtrak due to [{}]", e.getLocalizedMessage());
            newRoutes = null;
        } catch (IOException e) {
            log.error("AMTK-2299: Unable to load Amtrak due to download error [{}]", e.getLocalizedMessage());
            throw new RuntimeException(e);
        }
        return newRoutes;
    }

    /**
     * Will generate the response list of requested routes. If routesToLoad is null or empty,
     *  it is interpreted as asking for all routes.
     * @param routesToLoad  The routes asked for.
     * @return  List of available routes, ideally all of routesToLoad, but will always be the intersection of what
     *  is loaded.
     */
    private List<String> generateListOfRequestedRouteIds(String... routesToLoad) {
        if (routesToLoad == null || routesToLoad.length == 0) {
            return this.mapOfRoutes
                    .keySet()
                    .parallelStream()
                    .map(elem -> elem.toString())
                    .toList();
        } else {
            List<String> listifyForComparison = List.of(routesToLoad);
            return this.mapOfRoutes
                    .keySet()
                    .parallelStream()
                    .filter(listifyForComparison::contains)
                    .map(elem -> elem.toString())
                    .toList();
        }
    }

    /**
     * Will make a RESTful call to the Amtrak GTFS Data Store and pull down the GTFS.zip file to
     *  applications /tmp_dir/zip.
     * @return  True if successful.
     * @throws IOException  There was an issue in the pull chain.
     */
    public boolean refreshAmtrakData() throws IOException {
        Path zipLocation = this.amtrakDataClient.retrieveGtfsPayload();
        Path dataLocation = Path.of(this.temporaryDirectory.toString(), amtrakProperties.getGtfs().getDataDirectory());

        this.zipUtil.unzip(zipLocation.toString(), dataLocation.toString());

        return true;
    }

}
