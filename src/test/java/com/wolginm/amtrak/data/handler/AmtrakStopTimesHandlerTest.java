package com.wolginm.amtrak.data.handler;

import static org.mockito.ArgumentMatchers.any;

import java.io.FileInputStream;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.wolginm.amtrak.data.models.consolidated.Stop;
import com.wolginm.amtrak.data.models.gtfs.ICVMapable;
import com.wolginm.amtrak.data.models.gtfs.StopTimes;
import com.wolginm.amtrak.data.models.gtfs.Stops;
import com.wolginm.amtrak.data.properties.AmtrakProperties;
import com.wolginm.amtrak.data.util.CSVUtil;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class AmtrakStopTimesHandlerTest {
    
    @InjectMocks
    private AmtrakStopTimesHandler amtrakStopTimesHandler;

    @Mock
    private CSVUtil csvUtil;

    @Mock
    private AmtrakProperties amtrakProperties;

    @Nested
    @DisplayName("Load StopTimes From Files")
    class LoadStopTimesFromFiles {
        List<ICVMapable> stopsList;
        List<ICVMapable> stopTimesList;

        @BeforeEach
        public void setUp() {
            Mockito.when(amtrakProperties.getDataDirectory())
                .thenReturn("./data");
        }

        @Test
        public void loadStopsFromFiles() {
            stopsList = new ArrayList<>();
            stopTimesList = new ArrayList<>();
            Stops stops = new Stops();
            StopTimes stopTimes = new StopTimes();

            stops.setStop_id("PHL");
            stops.setStop_lat(0.0);
            stops.setStop_lon(0.0);
            stops.setStop_name("Philly");
            stops.setStop_timezone("Philly");
            stops.setStop_url("https://amtrak.com");

            stopTimes.setArrival_time(LocalTime.of(8, 30, 00));
            stopTimes.setDeparture_time(LocalTime.of(8, 35, 00));
            stopTimes.setDrop_off_type(1);
            stopTimes.setStop_id("PHL");
            stopTimes.setStop_sequence(1);
            stopTimes.setTrip_id("0000");

            stopsList.add(stops);
            stopTimesList.add(stopTimes);

            Mockito.when(csvUtil.csvToObject(any(FileInputStream.class),
                any(Stops.class))).thenReturn(stopsList);
            Mockito.when(csvUtil.csvToObject(any(FileInputStream.class),
                any(StopTimes.class))).thenReturn(stopTimesList);

            Assertions.assertDoesNotThrow(() -> 
                amtrakStopTimesHandler.loadRoutesFromFiles());
        }

        @Test
        public void loadStopsFromFiles_NoStopTime() {
            stopsList = new ArrayList<>();
            stopTimesList = new ArrayList<>();
            Stops stops = new Stops();

            stops.setStop_id("PHL");
            stops.setStop_lat(0.0);
            stops.setStop_lon(0.0);
            stops.setStop_name("Philly");
            stops.setStop_timezone("Philly");
            stops.setStop_url("https://amtrak.com");

            stopsList.add(stops);

            Mockito.when(csvUtil.csvToObject(any(FileInputStream.class),
                any(Stops.class))).thenReturn(stopsList);
            Mockito.when(csvUtil.csvToObject(any(FileInputStream.class),
                any(StopTimes.class))).thenReturn(stopTimesList);

            Assertions.assertDoesNotThrow(() -> 
                amtrakStopTimesHandler.loadRoutesFromFiles());
        }

        @Test
        public void loadStopsFromFiles_NoStops() {
            stopsList = new ArrayList<>();
            stopTimesList = new ArrayList<>();
            StopTimes stopTimes = new StopTimes();

            stopTimes.setArrival_time(LocalTime.of(8, 30, 00));
            stopTimes.setDeparture_time(LocalTime.of(8, 35, 00));
            stopTimes.setDrop_off_type(1);
            stopTimes.setStop_id("PHL");
            stopTimes.setStop_sequence(1);
            stopTimes.setTrip_id("0000");

            stopTimesList.add(stopTimes);

            Mockito.when(csvUtil.csvToObject(any(FileInputStream.class),
                any(Stops.class))).thenReturn(stopsList);
            Mockito.when(csvUtil.csvToObject(any(FileInputStream.class),
                any(StopTimes.class))).thenReturn(stopTimesList);

            Assertions.assertDoesNotThrow(() -> 
                amtrakStopTimesHandler.loadRoutesFromFiles());
        }

        @Test
        public void loadStopsFromFiles_NoneEither() {
            stopsList = new ArrayList<>();
            stopTimesList = new ArrayList<>();

            Mockito.when(csvUtil.csvToObject(any(FileInputStream.class),
                any(Stops.class))).thenReturn(stopsList);
            Mockito.when(csvUtil.csvToObject(any(FileInputStream.class),
                any(StopTimes.class))).thenReturn(stopTimesList);

            Assertions.assertDoesNotThrow(() -> 
                amtrakStopTimesHandler.loadRoutesFromFiles());
        }

        @Test
        public void loadStopsFromFiles_ThrowsFileNotFoundStops() {
            Mockito.when(csvUtil.csvToObject(any(FileInputStream.class),
                any(Stops.class)))
                .thenThrow(new RuntimeException("I'm a runtime exception!"));

            Assertions.assertThrows(RuntimeException.class, 
                () -> amtrakStopTimesHandler.loadRoutesFromFiles(),
                "Didn't throw the exception as expected");
        }

        @Test
        public void loadStopsFromFiles_ThrowsFileNotFoundStopTimes() {
            Mockito.when(csvUtil.csvToObject(any(FileInputStream.class),
                any(StopTimes.class)))
                .thenThrow(new RuntimeException("I'm a runtime exception!"));

            Assertions.assertThrows(RuntimeException.class, 
                () -> amtrakStopTimesHandler.loadRoutesFromFiles(),
                "Didn't throw the exception as expected");
        }
    }

    @Nested
    @DisplayName("Find all stop times by Trip ID")
    class FindAllStopTimesByTripId {
        List<ICVMapable> stopsList;
        List<ICVMapable> stopTimesList;

        @BeforeEach
        public void setUp() {
            Mockito.when(amtrakProperties.getDataDirectory())
                .thenReturn("./data");
        }

        @Test
        public void findRouteByRoutesId() {
            stopsList = new ArrayList<>();
            stopTimesList = new ArrayList<>();
            StopTimes stopTimes = new StopTimes();

            stopTimes.setArrival_time(LocalTime.of(8, 30, 00));
            stopTimes.setDeparture_time(LocalTime.of(8, 35, 00));
            stopTimes.setDrop_off_type(1);
            stopTimes.setStop_id("PHL");
            stopTimes.setStop_sequence(1);
            stopTimes.setTrip_id("0001");

            stopTimesList.add(stopTimes);

            Mockito.when(csvUtil.csvToObject(any(FileInputStream.class),
                any(Stops.class))).thenReturn(stopsList);
            Mockito.when(csvUtil.csvToObject(any(FileInputStream.class),
                any(StopTimes.class))).thenReturn(stopTimesList);

            Assertions.assertDoesNotThrow(() -> 
                amtrakStopTimesHandler.loadRoutesFromFiles());

            Assertions.assertIterableEquals(Arrays.asList(stopTimes), 
                amtrakStopTimesHandler.findAllStopTimesByTripId("0001"));
        }

        @Test
        public void findRouteByRoutesId_EmptyList() {
            stopsList = new ArrayList<>();
            stopTimesList = new ArrayList<>();

            Mockito.when(csvUtil.csvToObject(any(FileInputStream.class),
                any(Stops.class))).thenReturn(stopsList);
            Mockito.when(csvUtil.csvToObject(any(FileInputStream.class),
                any(StopTimes.class))).thenReturn(stopTimesList);

            Assertions.assertDoesNotThrow(() -> 
                amtrakStopTimesHandler.loadRoutesFromFiles());

            Assertions.assertIterableEquals(new ArrayList<>(), 
                amtrakStopTimesHandler.findAllStopTimesByTripId("0001"));
        }

        @Test
        public void findRouteByRoutesId_NoMatch() {
            stopsList = new ArrayList<>();
            stopTimesList = new ArrayList<>();
            StopTimes stopTimes = new StopTimes();

            stopTimes.setArrival_time(LocalTime.of(8, 30, 00));
            stopTimes.setDeparture_time(LocalTime.of(8, 35, 00));
            stopTimes.setDrop_off_type(1);
            stopTimes.setStop_id("PHL");
            stopTimes.setStop_sequence(1);
            stopTimes.setTrip_id("0002");

            stopTimesList.add(stopTimes);

            Mockito.when(csvUtil.csvToObject(any(FileInputStream.class),
                any(Stops.class))).thenReturn(stopsList);
            Mockito.when(csvUtil.csvToObject(any(FileInputStream.class),
                any(StopTimes.class))).thenReturn(stopTimesList);

            Assertions.assertDoesNotThrow(() -> 
                amtrakStopTimesHandler.loadRoutesFromFiles());

            Assertions.assertIterableEquals(new ArrayList<>(), 
                amtrakStopTimesHandler.findAllStopTimesByTripId("0001"));
        }
    }
    
    @Nested
    @DisplayName("Find all stops by Stop ID")
    class FindStopsByStopId {
        List<ICVMapable> stopsList;
        List<ICVMapable> stopTimesList;

        @BeforeEach
        public void setUp() {
            Mockito.when(amtrakProperties.getDataDirectory())
                .thenReturn("./data");
        }

        @Test
        public void findRouteByRoutesId() {
            stopsList = new ArrayList<>();
            stopTimesList = new ArrayList<>();
            Stops stops = new Stops();

            stops.setStop_id("PHL");
            stops.setStop_lat(0.0);
            stops.setStop_lon(0.0);
            stops.setStop_name("Philly");
            stops.setStop_timezone("Philly");
            stops.setStop_url("https://amtrak.com");

            stopsList.add(stops);

            Mockito.when(csvUtil.csvToObject(any(FileInputStream.class),
                any(Stops.class))).thenReturn(stopsList);
            Mockito.when(csvUtil.csvToObject(any(FileInputStream.class),
                any(StopTimes.class))).thenReturn(stopTimesList);

            Assertions.assertDoesNotThrow(() -> 
                amtrakStopTimesHandler.loadRoutesFromFiles());

            Assertions.assertEquals(stops, 
                amtrakStopTimesHandler.findStopsByStopId("PHL"));
        }

        @Test
        public void findRouteByRoutesId_EmptyList() {
            stopsList = new ArrayList<>();
            stopTimesList = new ArrayList<>();

            Mockito.when(csvUtil.csvToObject(any(FileInputStream.class),
                any(Stops.class))).thenReturn(stopsList);
            Mockito.when(csvUtil.csvToObject(any(FileInputStream.class),
                any(StopTimes.class))).thenReturn(stopTimesList);

            Assertions.assertDoesNotThrow(() -> 
                amtrakStopTimesHandler.loadRoutesFromFiles());

            Assertions.assertEquals(null, 
                amtrakStopTimesHandler.findStopsByStopId("PHL"));
        }

        @Test
        public void findRouteByRoutesId_NoMatch() {
            stopsList = new ArrayList<>();
            stopTimesList = new ArrayList<>();
            Stops stops = new Stops();

            stops.setStop_id("PHL");
            stops.setStop_lat(0.0);
            stops.setStop_lon(0.0);
            stops.setStop_name("Philly");
            stops.setStop_timezone("Philly");
            stops.setStop_url("https://amtrak.com");

            stopsList.add(stops);

            Mockito.when(csvUtil.csvToObject(any(FileInputStream.class),
                any(Stops.class))).thenReturn(stopsList);
            Mockito.when(csvUtil.csvToObject(any(FileInputStream.class),
                any(StopTimes.class))).thenReturn(stopTimesList);

            Assertions.assertDoesNotThrow(() -> 
                amtrakStopTimesHandler.loadRoutesFromFiles());

            Assertions.assertEquals(null, 
                amtrakStopTimesHandler.findStopsByStopId("LHP"));
        }
    }

    @Nested
    @DisplayName("Find all stops by Trip ID")
    class FindStopsByStopsByTripId {
        List<ICVMapable> stopsList;
        List<ICVMapable> stopTimesList;

        @BeforeEach
        public void setUp() {
            Mockito.when(amtrakProperties.getDataDirectory())
                .thenReturn("./data");
        }

        @Test
        public void findAllStopsByTripId() {
            stopsList = new ArrayList<>();
            stopTimesList = new ArrayList<>();
            Stops stops = new Stops();
            StopTimes stopTimes = new StopTimes();
            Stop stop;

            stops.setStop_id("PHL");
            stops.setStop_lat(0.0);
            stops.setStop_lon(0.0);
            stops.setStop_name("Philly");
            stops.setStop_timezone("Philly");
            stops.setStop_url("https://amtrak.com");

            stopTimes.setArrival_time(LocalTime.of(8, 30, 00));
            stopTimes.setDeparture_time(LocalTime.of(8, 35, 00));
            stopTimes.setDrop_off_type(1);
            stopTimes.setStop_id("PHL");
            stopTimes.setStop_sequence(1);
            stopTimes.setTrip_id("0000");

            stop = new Stop(stops, stopTimes);
            stopsList.add(stops);
            stopTimesList.add(stopTimes);

            Mockito.when(csvUtil.csvToObject(any(FileInputStream.class),
                any(Stops.class))).thenReturn(stopsList);
            Mockito.when(csvUtil.csvToObject(any(FileInputStream.class),
                any(StopTimes.class))).thenReturn(stopTimesList);
            
            Assertions.assertDoesNotThrow(() -> 
                amtrakStopTimesHandler.loadRoutesFromFiles());
            Assertions.assertIterableEquals(Arrays.asList(stop),  
                amtrakStopTimesHandler.findAllStopsByTripId("0000"));
        }

        @Test
        public void findRouteByRoutesId_EmptyList() {
            stopsList = new ArrayList<>();
            stopTimesList = new ArrayList<>();
            Stops stops = new Stops();

            stops.setStop_id("PHL");
            stops.setStop_lat(0.0);
            stops.setStop_lon(0.0);
            stops.setStop_name("Philly");
            stops.setStop_timezone("Philly");
            stops.setStop_url("https://amtrak.com");

            stopsList.add(stops);

            Mockito.when(csvUtil.csvToObject(any(FileInputStream.class),
                any(Stops.class))).thenReturn(stopsList);
            Mockito.when(csvUtil.csvToObject(any(FileInputStream.class),
                any(StopTimes.class))).thenReturn(stopTimesList);

            Assertions.assertDoesNotThrow(() -> 
                amtrakStopTimesHandler.loadRoutesFromFiles());
            Assertions.assertIterableEquals(Arrays.asList(),  
                amtrakStopTimesHandler.findAllStopsByTripId("0000"));
        }

        @Test
        public void findRouteByRoutesId_NoStops() {
            stopsList = new ArrayList<>();
            stopTimesList = new ArrayList<>();
            StopTimes stopTimes = new StopTimes();

            stopTimes.setArrival_time(LocalTime.of(8, 30, 00));
            stopTimes.setDeparture_time(LocalTime.of(8, 35, 00));
            stopTimes.setDrop_off_type(1);
            stopTimes.setStop_id("PHL");
            stopTimes.setStop_sequence(1);
            stopTimes.setTrip_id("0000");

            stopTimesList.add(stopTimes);

            Mockito.when(csvUtil.csvToObject(any(FileInputStream.class),
                any(Stops.class))).thenReturn(stopsList);
            Mockito.when(csvUtil.csvToObject(any(FileInputStream.class),
                any(StopTimes.class))).thenReturn(stopTimesList);

            Assertions.assertDoesNotThrow(() -> 
                amtrakStopTimesHandler.loadRoutesFromFiles());
            Assertions.assertIterableEquals(Arrays.asList(), 
                amtrakStopTimesHandler.findAllStopsByTripId("0000"));
        }
    }
}
