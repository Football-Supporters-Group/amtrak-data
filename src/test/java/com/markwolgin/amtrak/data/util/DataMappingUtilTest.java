package com.markwolgin.amtrak.data.util;

import com.markwolgin.amtrak.data.models.*;
import com.markwolgin.amtrak.data.models.Calendar;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.util.*;
import java.util.stream.Collectors;


class DataMappingUtilTest {


    private List<Stops> stops;
    private List<Trips> trips;
    private List<Routes> routes;
    private List<Shapes> shapes;
    private List<Calendar> calendars;
    private List<StopTimes> stopTimes;
    private Map<String, LinkedHashSet<String>> routeMetaData;
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
            add(new StopTimes().stopId("HAR").tripId("1000"));
            add(new StopTimes().stopId("PHL").tripId("1000"));
            add(new StopTimes().stopId("NYP").tripId("1000"));
            add(new StopTimes().stopId("HAR").tripId("1001"));
            add(new StopTimes().stopId("PAO").tripId("1001"));
            add(new StopTimes().stopId("PHL").tripId("1001"));
            add(new StopTimes().stopId("NYP").tripId("1001"));
            add(new StopTimes().stopId("ABC").tripId("2000"));
            add(new StopTimes().stopId("PDX").tripId("2000"));
            add(new StopTimes().stopId("SEA").tripId("2000"));
            add(new StopTimes().stopId("HAR").tripId("2000"));
            add(new StopTimes().stopId("PHL").tripId("2000"));
        }};
        trips = new LinkedList<>(){{
           add(new Trips().tripShortName(1010L).tripId("1000").directionId(1).routeId("999").serviceId("-1").shapeId("-1").tripHeadsign("Keystone Limt'd"));
           add(new Trips().tripShortName(1011L).tripId("1001").directionId(1).routeId("999").serviceId("-1").shapeId("-1").tripHeadsign("Keystone Limt'd"));
           add(new Trips().tripShortName(2001L).tripId("2000").directionId(1).routeId("888").serviceId("-1").shapeId("-1").tripHeadsign("Coast Starlight"));
        }};

        routes = new LinkedList<>(){{
            add(new Routes().routeType(-1).routeId("999").routeShortName("Keystone").routeLongName("Keystone Lmt'd").agencyId(1).routeColor("BLUE").routeTextColor("GRAY").routeUrl(URI.create("https://amtrak.com")));
            add(new Routes().routeType(-1).routeId("888").routeShortName("Coast Starlight").routeLongName("Coast Starlight").agencyId(1).routeColor("GREEN").routeTextColor("GRAY").routeUrl(URI.create("https://amtrak.com")));
        }};

        calendars = new LinkedList<>() {{
           add(new Calendar().serviceId("-1").monday(1).tuesday(1).wednesday(1).thursday(1).friday(1).saturday(1).sunday(1).startDate("20200301").endDate("20690420"));
        }};

        routeMetaData = new HashMap<>(){{
            put("999", new LinkedHashSet<String>(){{
                add("NYP");
                add("PHL");
                add("PAO");
                add("HAR");
            }});
            put("888", new LinkedHashSet<String>(){{
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
        Map<String, ConsolidatedTrip> actual = this.dataMappingUtil.buildConsolidatedTripMap(stopTimes, calendars, trips, null);

        Assertions.assertNotNull(actual);
        Assertions.assertEquals(3, actual.size());
        ConsolidatedTrip keystoneTrip = actual.get("1000");
        Assertions.assertNotNull(keystoneTrip);
        Assertions.assertEquals(3, keystoneTrip.getTripStops().size());
    }

    @Test
    @Order(2)
    void buildConsolidatedRouteMap() {
        Map<String, ConsolidatedTrip> consolidatedTripMap = this.dataMappingUtil.buildConsolidatedTripMap(stopTimes, calendars, trips, null);
        Map<String, ConsolidatedRoute> actual = this.dataMappingUtil.buildConsolidatedRouteMap(trips, consolidatedTripMap, routes, calendars, stops.stream().collect(Collectors.toMap(Stops::getStopId, t->t)), routeMetaData);

        Assertions.assertNotNull(actual);
        Assertions.assertEquals(2, actual.size());
        Assertions.assertEquals(consolidatedTripMap.get("1000"), actual.get("999").getTripList().get().get("1000"));
        Assertions.assertEquals(4, actual.get("999").getTripList().get().get("1001").getTripStops().size());

        Assertions.assertTrue(actual.get("999").getTripList().get().get("1000").getOperatingPattern().getMonday());
        Assertions.assertTrue(actual.get("999").getTripList().get().get("1000").getOperatingPattern().getTuesday());
        Assertions.assertTrue(actual.get("999").getTripList().get().get("1000").getOperatingPattern().getWednesday());
        Assertions.assertTrue(actual.get("999").getTripList().get().get("1000").getOperatingPattern().getThursday());
        Assertions.assertTrue(actual.get("999").getTripList().get().get("1000").getOperatingPattern().getFriday());
        Assertions.assertTrue(actual.get("999").getTripList().get().get("1000").getOperatingPattern().getSaturday());
        Assertions.assertTrue(actual.get("999").getTripList().get().get("1000").getOperatingPattern().getSunday());
    }

    @Test
    @Order(3)
    void buildConsolidatedRouteMap_MissingCalendar() {
        this.calendars.get(0).setServiceId("1234");
        Map<String, ConsolidatedTrip> consolidatedTripMap = this.dataMappingUtil.buildConsolidatedTripMap(stopTimes, calendars, trips, null);
        Map<String, ConsolidatedRoute> actual = this.dataMappingUtil.buildConsolidatedRouteMap(trips, consolidatedTripMap, routes, calendars, stops.stream().collect(Collectors.toMap(Stops::getStopId, t->t)), routeMetaData);

        Assertions.assertNotNull(actual);
        Assertions.assertEquals(2, actual.size());
        Assertions.assertEquals(consolidatedTripMap.get("1000"), actual.get("999").getTripList().get().get("1000"));
        Assertions.assertEquals(4, actual.get("999").getTripList().get().get("1001").getTripStops().size());

        Assertions.assertFalse(actual.get("999").getTripList().get().get("1000").getOperatingPattern().getMonday());
        Assertions.assertFalse(actual.get("999").getTripList().get().get("1000").getOperatingPattern().getTuesday());
        Assertions.assertFalse(actual.get("999").getTripList().get().get("1000").getOperatingPattern().getWednesday());
        Assertions.assertFalse(actual.get("999").getTripList().get().get("1000").getOperatingPattern().getThursday());
        Assertions.assertFalse(actual.get("999").getTripList().get().get("1000").getOperatingPattern().getFriday());
        Assertions.assertFalse(actual.get("999").getTripList().get().get("1000").getOperatingPattern().getSaturday());
        Assertions.assertFalse(actual.get("999").getTripList().get().get("1000").getOperatingPattern().getSunday());
    }
}