package com.wolginm.amtrak.data.handler;

import static org.mockito.ArgumentMatchers.any;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

import com.wolginm.amtrak.data.models.gtfs.Calendar;
import com.wolginm.amtrak.data.models.gtfs.ICVMapable;
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

public class AmtrakCalendarHandlerTest {
    
    @InjectMocks
    private AmtrakCalendarHandler amtrakCalendarHandler;

    @Mock
    private CSVUtil csvUtil;

    @Mock
    private AmtrakProperties amtrakProperties;

    @Nested
    @DisplayName("Load Routes From Files")
    class LoadRoutesFromFiles {

        @BeforeEach
        public void setUp() {
            Mockito.when(amtrakProperties.getDataDirectory())
                .thenReturn("./data");
        }

        @Test
        public void loadRoutesFromFiles() {
            List<ICVMapable> calendarList = new ArrayList<>();
            Calendar calendar = new Calendar();
            calendar.setService_id("00000001");
            calendar.setMonday(true);
            calendar.setTuesday(true);
            calendar.setWednesday(true);
            calendar.setThursday(true);
            calendar.setFriday(true);
            calendar.setSaturday(true);
            calendar.setSunday(true);
            calendarList.add(calendar);

            Mockito.when(csvUtil.csvToObject(any(FileInputStream.class),
                any(Calendar.class))).thenReturn(calendarList);

            Assertions.assertDoesNotThrow(() -> 
                amtrakCalendarHandler.loadRoutesFromFiles());
        }

        @Test
        public void loadRoutesFromFiles_NothingInList() {
            List<ICVMapable> calendarList = new ArrayList<>();

            Mockito.when(csvUtil.csvToObject(any(FileInputStream.class),
                any(Calendar.class))).thenReturn(calendarList);

            Assertions.assertDoesNotThrow(() -> 
                amtrakCalendarHandler.loadRoutesFromFiles());
        }

        @Test
        public void loadRoutesFromFiles_ThrowsFileNotFound() {
            Mockito.when(csvUtil.csvToObject(any(FileInputStream.class),
                any(Calendar.class)))
                .thenThrow(new RuntimeException("I'm a runtime exception!"));

            Assertions.assertThrows(RuntimeException.class, 
                () -> amtrakCalendarHandler.loadRoutesFromFiles(),
                "Didn't throw the exception as expected");
        }
    }

    @Nested
    @DisplayName("Find Route By ServiceId")
    class FindRouteByServiceId {

        List<ICVMapable> calendarList;
        Calendar calendar;

        @BeforeEach
        public void setUp() {
            calendarList = new ArrayList<>();
            Mockito.when(amtrakProperties.getDataDirectory())
                .thenReturn("./data");
        }

        @Test
        public void findRouteByServiceId() {
            calendar = new Calendar();
            calendar.setService_id("00000001");
            calendar.setMonday(true);
            calendar.setTuesday(true);
            calendar.setWednesday(true);
            calendar.setThursday(true);
            calendar.setFriday(true);
            calendar.setSaturday(true);
            calendar.setSunday(true);
            calendarList.add(calendar);

            Mockito.when(csvUtil.csvToObject(any(FileInputStream.class),
                any(Calendar.class))).thenReturn(calendarList);

            Assertions.assertDoesNotThrow(() -> 
                amtrakCalendarHandler.loadRoutesFromFiles());
            Assertions.assertEquals(this.calendar,
                amtrakCalendarHandler.findRouteByServiceId("00000001"));
        }

        @Test
        public void findRouteByServiceId_NoMatch() {
            calendar = new Calendar();
            calendar.setService_id("00000001");
            calendar.setMonday(true);
            calendar.setTuesday(true);
            calendar.setWednesday(true);
            calendar.setThursday(true);
            calendar.setFriday(true);
            calendar.setSaturday(true);
            calendar.setSunday(true);
            calendarList.add(calendar);

            Mockito.when(csvUtil.csvToObject(any(FileInputStream.class),
                any(Calendar.class))).thenReturn(calendarList);


            Assertions.assertDoesNotThrow(() -> 
                amtrakCalendarHandler.loadRoutesFromFiles());
            Assertions.assertNull(amtrakCalendarHandler.findRouteByServiceId("00000002"));
        }

        @Test
        public void findRouteByServiceId_NothingInList() {
            Mockito.when(csvUtil.csvToObject(any(FileInputStream.class),
                any(Calendar.class))).thenReturn(calendarList);

            Assertions.assertDoesNotThrow(() -> 
                amtrakCalendarHandler.loadRoutesFromFiles());
            Assertions.assertNull(amtrakCalendarHandler.findRouteByServiceId("00000002"));
        }
    }
}
