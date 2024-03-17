package com.markwolgin.amtrak.data.util;

import com.markwolgin.amtrak.data.models.*;
import com.markwolgin.amtrak.data.models.Calendar;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * The utility for converting the raw Amtrak data into less raw Consolidated Data.
 */
@Slf4j
@Component
public class DataMappingUtil {


    protected final static Calendar DEFAULT_MISSING_CALENDAR = new Calendar("REPLACE_ME", 0, 0, 0, 0, 0, 0, 0, "19700101", "29991231");

    /**
     * Will build a map of TripId -> {@link ConsolidatedTrip}.
     * @param stopTimes List of {@link StopTimes}.
     * @param calendars List of {@link Calendar}.
     * @param trips     List of {@link Trips}.
     * @param shapes    List of {@link Shapes}.
     * @return          The map of TripId -> {@link ConsolidatedTrip}.
     */
    public Map<String, ConsolidatedTrip> buildConsolidatedTripMap(final List<StopTimes> stopTimes,
                                                                   final List<Calendar> calendars,
                                                                   final List<Trips> trips,
                                                                   final List<Shapes> shapes) {
        log.info("AMTK-6600: Attempting to construct map of [{}] Consolidated Trips", trips.size());
        Trips trip;
        ConsolidatedTrip consolidatedTrip;
        Calendar calendar;
        String startDate, endDate;
        Map<String, ConsolidatedTrip> consolidatedTripMap = new HashMap<>(trips.size());
        Map<String, Calendar> calendarServiceMap = calendars.parallelStream().collect(Collectors.toMap(elem -> elem.getServiceId(), elem -> elem));
        Map<String, Trips> tripsMap = trips.parallelStream().collect(Collectors.toMap(Trips::getTripId, t->t));
        for (StopTimes stoptime: stopTimes) {
            consolidatedTrip = consolidatedTripMap.get(stoptime.getTripId());
            if (consolidatedTrip == null) {
                log.debug("AMTK-6611: Trip not in map, creating and setting trip [{}]", stoptime.getTripId());
                trip = tripsMap.get(stoptime.getTripId());
                calendar = calendarServiceMap.getOrDefault(trip.getServiceId(), DEFAULT_MISSING_CALENDAR.serviceId(trip.getServiceId()));
                startDate = calendar.getStartDate();
                endDate = calendar.getEndDate();
                consolidatedTrip = new ConsolidatedTrip()
                        .tripId(trip.getTripId())
                        .tripHeadsign(trip.getTripHeadsign())
                        .tripShortName(trip.getTripShortName())
                        .directionId(trip.getDirectionId())
                        .operatingPattern(new OperatingPattern()
                                .monday(calendar.getMonday() != 0)
                                .tuesday(calendar.getTuesday() != 0)
                                .wednesday(calendar.getWednesday() != 0)
                                .thursday(calendar.getThursday() != 0)
                                .friday(calendar.getFriday() != 0)
                                .saturday(calendar.getSaturday() != 0)
                                .sunday(calendar.getSunday() != 0)
                        )
                        .routeId(trip.getRouteId())
                        .serviceId(trip.getServiceId())
                        .shapeId(trip.getShapeId())
                        .tripEffectiveOnDate(LocalDate.of(
                                Integer.parseInt(startDate.substring(0, 4)),
                                Integer.parseInt(startDate.substring(4, 6)),
                                Integer.parseInt(startDate.substring(6))))
                        .tripNoLongerEffectiveOnDate(LocalDate.of(
                                Integer.parseInt(endDate.substring(0, 4)),
                                Integer.parseInt(endDate.substring(4, 6)),
                                Integer.parseInt(endDate.substring(6))));
                consolidatedTripMap.put(consolidatedTrip.getTripId(), consolidatedTrip);
            }
            consolidatedTrip.addTripStopsItem(stoptime);
        }

        log.info("AMTK-6610: Constructed map of [{}] Consolidated Trips", consolidatedTripMap.size());
        return consolidatedTripMap;
    }

    /**
     * Will build a map of {@link ConsolidatedRoute} (RouteIds -> {@link ConsolidatedRoute}).
     *  This is a parallized method, and would love to use as much CPU as the JVM feels comfortable throwing at it.
     *  That said, on my plucky little Dell XPS15 9560 can chug through it no prob.
     * @param trips                 List of {@link Trips}
     * @param consolidatedTripMap   Mapping of tripId -> {@link ConsolidatedTrip}
     * @param routes                List of Routes {@link Routes}
     * @param calendars             List of {@link Calendar}
     * @param stops                 Map StopIds -> {@link Stops}
     * @return                      The Mapping on RouteIds -> {@link ConsolidatedRoute}
     */
    public Map<String, ConsolidatedRoute> buildConsolidatedRouteMap(final List<Trips> trips,
                                                                     final Map<String, ConsolidatedTrip> consolidatedTripMap,
                                                                     final List<Routes> routes,
                                                                     final List<Calendar> calendars,
                                                                     final Map<String, Stops> stops,
                                                                     final Map<String, LinkedHashSet<String>> routeOrderMetaData) {
        log.info("AMTK-6600: Attempting to construct map of [{}] Consolidated Routes", routes.size());

        Map<String, ConsolidatedRoute> consolidatedRouteMap = new HashMap<>(routes.size());
        Map<String, List<ConsolidatedTrip>> routeIdToConsolidatedTripList = this.buildRouteIdToConsolidatedTripList(consolidatedTripMap, routes.size());

        routes
                .stream()
                .forEach(route -> {
            consolidatedRouteMap.put(route.getRouteId(), new ConsolidatedRoute()
                    .routeId(route.getRouteId())
                    .routeColor(route.getRouteColor())
                    .routeTextColor(route.getRouteTextColor())
                    .routeType(route.getRouteType())
                    .routeUrl(route.getRouteUrl())
                    .routeLongName(route.getRouteLongName())
                    .routeShortName(route.getRouteShortName())
                    /*
                     * Will take the routeId to ConsolidatedTrip List
                     *      get te Route ID
                     *      stream it
                     *      use lambda mappers to create a bijection of tripId <-> trip.
                     *
                     * Note:  tripList is a *bit* of a misnamed variable.
                     */
                    .tripList(routeIdToConsolidatedTripList.get(route.getRouteId())
                            .parallelStream()
                            .collect(Collectors.toMap(t->t.getTripId(), t->t)))
                    /*
                     * Okay this one needs to me explanation.
                     * We start with the routeId to ConsolidatedTripList and need to get a map of all possible route stops.
                     *      Get the specific list of consolidated trips with routeId
                     *      using lambda to map the list element (ConsolidatedTrip) into a list of all possible stops.
                     *          So, ConsolidatedTrip has a list of StopTimes.  StopTimes has a stopId.
                     *      We now have a list of the stops on a trip, per trip (so for n trips, we have n lists)
                     *      Taking advantage of javas lambda .disticnt pipe, we remove dups.
                     *      Now with our stream of lists, we use another map and the acquired stopIds to get specific stops.
                     *      Finally, we collect all distinct stops into a new map, a bijection of stopId <-> Stop.
                     */
                    .allStops(routeIdToConsolidatedTripList
                            .get(route.getRouteId())
                            .parallelStream()
                            .map(consolidatedTrip -> {
                                return consolidatedTrip.getTripStops()
                                        .parallelStream()
                                        .map(StopTimes::getStopId)
                                        .distinct()
                                        .toList();
                            })
                            .flatMap(List::stream)
                            .distinct()
                            .collect(Collectors.toMap(t->t, stops::get)))
                    .stopOrder(routeOrderMetaData.getOrDefault(route.getRouteId(), new LinkedHashSet<String>()).stream().collect(Collectors.toUnmodifiableList()))
            );
        });
        log.info("AMTK-6610: Constructed map of [{}] Consolidated Routes", routes.size());
        return consolidatedRouteMap;
    }

    /**
     * Will build a map of route id -> list of possible trips
     * @param consolidatedTripMap   Trip Map
     * @param size                  Max number of routes, to reduce array copies.
     * @return                      The expected map.
     */
    private Map<String, List<ConsolidatedTrip>> buildRouteIdToConsolidatedTripList(Map<String, ConsolidatedTrip> consolidatedTripMap, int size) {
        Map<String, List<ConsolidatedTrip>> routeIdToConsolidatedTripList = new HashMap<>(size);
        String routeId;
        for (ConsolidatedTrip consolidatedTrip: consolidatedTripMap.values()) {
            routeId = consolidatedTrip.getRouteId();
            List<ConsolidatedTrip> consolidatedTripList = routeIdToConsolidatedTripList.get(routeId);
            if (consolidatedTripList == null) {
                consolidatedTripList = new LinkedList<>();
                routeIdToConsolidatedTripList.put(routeId, consolidatedTripList);
            }
            consolidatedTripList.add(consolidatedTrip);
        }
        return routeIdToConsolidatedTripList;
    }

    /**
     * Will turn a list of stops into a map of stop name -> Stops.class.
     * @param stops List of stops.
     * @return      Map of stop name -> Stops.class.
     */
    public Map<String, Stops> stopsMap(final List<Stops> stops) {
        return stops
                .stream()
                .collect(Collectors.toMap(Stops::getStopId, t->t));
    }

}
