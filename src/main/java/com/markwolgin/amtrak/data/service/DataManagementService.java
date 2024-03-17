package com.markwolgin.amtrak.data.service;

import com.markwolgin.amtrak.data.client.AmtrakDataClient;
import com.markwolgin.amtrak.data.properties.AmtrakProperties;
import com.markwolgin.amtrak.data.util.DataMappingUtil;
import com.markwolgin.amtrak.data.util.FileUtil;
import com.markwolgin.amtrak.data.util.ZipUtil;
import com.markwolgin.amtrak.data.models.*;
import lombok.extern.slf4j.Slf4j;
import org.openapitools.jackson.nullable.JsonNullable;
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
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@Slf4j
@Service
public class DataManagementService {

    private final InflationService inflationService;
    private final AmtrakDataClient amtrakDataClient;
    private final AmtrakProperties amtrakProperties;
    private final DataMappingUtil dataMappingUtil;
    private final Path temporaryDirectory;
    private final FileUtil fileUtil;
    private final ZipUtil zipUtil;

    private AtomicReference<Map<String, ConsolidatedRoute>> mapOfRoutes;

    private Instant lastRefresh;

    public DataManagementService(final InflationService inflationService,
                                 final AmtrakDataClient amtrakDataClient,
                                 final FileUtil fileUtil,
                                 final ZipUtil zipUtil,
                                 final DataMappingUtil dataMappingUtil,
                                 final AmtrakProperties amtrakProperties,
                                 @Qualifier("temporaryDirectoryPath") final Path tempDir) {
        this.inflationService = inflationService;
        this.amtrakDataClient = amtrakDataClient;
        this.amtrakProperties = amtrakProperties;
        this.dataMappingUtil = dataMappingUtil;
        this.temporaryDirectory = tempDir;
        this.fileUtil = fileUtil;
        this.zipUtil = zipUtil;
        this.lastRefresh = Instant.MIN;
        this.mapOfRoutes = new AtomicReference<>(Map.of());
    }

    public ConsolidatedResponseObject buildConsolidatedResponseObject(String... routesToLoad) {
        log.info("AMTK-2210: In buildConsolidatedResponseObject, will start processing request for routes: [{}]", List.of(routesToLoad));
        if (mapOfRoutes == null || mapOfRoutes.get().isEmpty()) {
            this.mapOfRoutes.set(this.loadAmtrakDataIntoLocal());
            if (mapOfRoutes == null || mapOfRoutes.get().isEmpty()) {
                String error = "AMTK-2299: Unable to load in Amtrak Data!  All further responses will be invalid until corrected.";
                log.error(error);
                this.mapOfRoutes.set(Map.of());
            }
        } else log.info("AMTK-2212: Data is loaded");
        log.debug("*------------------------------CHECKING LIST---------------------------------*");
        ConsolidatedResponseObject responseObject = new ConsolidatedResponseObject();
        responseObject.setLastTimeDataWasRefreshed(lastRefresh.toString());
        responseObject.setTimestamp(Instant.now().toString());
        responseObject.setRequestedRouteIds(this.generateListOfRequestedRouteIds(routesToLoad));
        responseObject.setRequestedConsolidatedRoutes(JsonNullable.of(this.mapOfRoutes.get()
                .entrySet()
                .stream()
                .filter(elem -> {
                    log.debug("Checking if route will be included [{}:{}]", String.format("%06d", Integer.parseInt(elem.getKey())), responseObject
                            .getRequestedRouteIds()
                            .contains(elem.getKey().toString()));
                    return responseObject
                            .getRequestedRouteIds()
                            .contains(elem.getKey().toString());
                })
                .collect(Collectors.toMap((Map.Entry<String, ConsolidatedRoute> elem) -> elem.getKey().toString(),
                        (Map.Entry<String, ConsolidatedRoute> elem) -> elem.getValue()))));
        log.debug("*---------------------------------CHECKED------------------------------------*");



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

    protected <T extends AmtrakObject> Map<String, ConsolidatedRoute> loadAmtrakDataIntoLocal() {
        Map<String, ConsolidatedRoute> newRoutes = null;
        try {
            this.refreshAmtrakData();

            Map<Class<T>, List<T>> allAmtrakObjects = this.inflationService.inflateAllAmtrakObjects();
            Map<String, LinkedHashSet<String>> routeMetaData = this.inflationService.inflateRouteOrderMetadata();

            //StopTimes, Calendars(Nullable), Trips, Shapes(Nullable)
            Map<String, ConsolidatedTrip> consolidatedTripIntermediate
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
            return this.mapOfRoutes.get()
                    .keySet()
                    .parallelStream()
                    .map(elem -> elem.toString())
                    .toList();
        } else {
            List<String> listifyForComparison = List.of(routesToLoad);
            return this.mapOfRoutes.get()
                    .keySet()
                    .parallelStream()
                    .map(elem -> elem.toString())
                    .filter(string -> listifyForComparison.contains(string))
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
        Path dataLocation = this.fileUtil.prepFoldersForFile(amtrakProperties.getGtfs().getDataDirectory());

        this.zipUtil.unzip(zipLocation.toString(), dataLocation.toString());

        return true;
    }

}
