package com.wolginm.amtrak.data.util;

import com.wolginmark.amtrak.data.models.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;


class DataMappingUtilTest {


    private List<Stops> stops;
    private List<Trips> trips;
    private List<Routes> routes;
    private List<Shapes> shapes;
    private List<StopTimes> stopTimes;
    private Map<Integer, LinkedHashSet<String>> routeMetaData;
    private DataMappingUtil dataMappingUtil;

    @BeforeEach
    void setUp() {
        stops = new LinkedList<>(){{
            add(new Stops().stopId("ABC"));
            add(new Stops().stopId("HAR"));
            add(new Stops().stopId("MAI"));
            add(new Stops().stopId("PAO"));
            add(new Stops().stopId("PDX"));
            add(new Stops().stopId("PHL"));
            add(new Stops().stopId("NYP"));
            add(new Stops().stopId("SEA"));
        }};
        stopTimes = new LinkedList<>(){{
            add(new StopTimes().stopId("HAR").tripId(1000L));
            add(new StopTimes().stopId("PHL").tripId(1000L));
            add(new StopTimes().stopId("NYP").tripId(1000L));
            add(new StopTimes().stopId("HAR").tripId(1001L));
            add(new StopTimes().stopId("PAO").tripId(1001L));
            add(new StopTimes().stopId("PHL").tripId(1001L));
            add(new StopTimes().stopId("NYP").tripId(1001L));
            add(new StopTimes().stopId("ABC").tripId(2000L));
            add(new StopTimes().stopId("PDX").tripId(2000L));
            add(new StopTimes().stopId("SEA").tripId(2000L));
            add(new StopTimes().stopId("HAR").tripId(2000L));
            add(new StopTimes().stopId("PHL").tripId(2000L));
        }};
        trips = new LinkedList<>(){{
           add(new Trips().tripShortName(1010L).tripId(1000L).directionId(1).routeId(999).serviceId(-1).shapeId(-1).tripHeadsign("Keystone Limt'd"));
           add(new Trips().tripShortName(1011L).tripId(1001L).directionId(1).routeId(999).serviceId(-1).shapeId(-1).tripHeadsign("Keystone Limt'd"));
           add(new Trips().tripShortName(2001L).tripId(2000L).directionId(1).routeId(888).serviceId(-1).shapeId(-1).tripHeadsign("Coast Starlight"));
        }};

        routes = new LinkedList<>(){{
            add(new Routes().routeType(-1).routeId(999).routeShortName("Keystone").routeLongName("Keystone Lmt'd").agencyId(1).routeColor("BLUE").routeTextColor("GRAY").routeUrl(URI.create("https://amtrak.com")));
            add(new Routes().routeType(-1).routeId(888).routeShortName("Coast Starlight").routeLongName("Coast Starlight").agencyId(1).routeColor("GREEN").routeTextColor("GRAY").routeUrl(URI.create("https://amtrak.com")));
        }};

        routeMetaData = new HashMap<>(){{
            put(999, new LinkedHashSet<String>(){{
                add("NYP");
                add("PHL");
                add("PAO");
                add("HAR");
            }});
            put(888, new LinkedHashSet<String>(){{
                add("PHL");
                add("HAR");
                add("SEA");
                add("PDX");
                add("ABC");
            }});

        }};

        dataMappingUtil = new DataMappingUtil();
    }

    @Test
    @Order(1)
    void buildConsolidatedTripMap() {
        Map<Integer, ConsolidatedTrip> actual = this.dataMappingUtil.buildConsolidatedTripMap(stopTimes, null, trips, null);

        Assertions.assertNotNull(actual);
        Assertions.assertEquals(3, actual.size());
        ConsolidatedTrip keystoneTrip = actual.get(1000);
        Assertions.assertNotNull(keystoneTrip);
        Assertions.assertEquals(3, keystoneTrip.getTripStops().size());
    }

    @Test
    @Order(2)
    void buildConsolidatedRouteMap() {
        Map<Integer, ConsolidatedTrip> consolidatedTripMap = this.dataMappingUtil.buildConsolidatedTripMap(stopTimes, null, trips, null);
        Map<Integer, ConsolidatedRoute> actual = this.dataMappingUtil.buildConsolidatedRouteMap(trips, consolidatedTripMap, routes, null, stops.stream().collect(Collectors.toMap(Stops::getStopId, t->t)), routeMetaData);

        Assertions.assertNotNull(actual);
        Assertions.assertEquals(2, actual.size());
        Assertions.assertEquals(consolidatedTripMap.get(1000), actual.get(999).getTripList().get().get("1000"));
        Assertions.assertEquals(4, actual.get(999).getTripList().get().get("1001").getTripStops().size());
    }
}