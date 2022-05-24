package com.wolginm.amtrak.data.handler;

import static org.mockito.ArgumentMatchers.any;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

import com.wolginm.amtrak.data.models.gtfs.ICVMapable;
import com.wolginm.amtrak.data.models.gtfs.Routes;
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
public class AmtrakRoutesHandlerTest {
    
    @InjectMocks
    private AmtrakRoutesHandler amtrakRoutesHandler;

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
            List<ICVMapable> routesList = new ArrayList<>();
            Routes routes = new Routes();
            routes.setAgency_id(123456);
            routes.setRoute_color("FFFFFF");
            routes.setRoute_text_color("FFFFFF");
            routes.setRoute_id(654321);
            routes.setRoute_long_name("Acela Express");
            routes.setRoute_short_name("Acela");
            routes.setRoute_type("2");
            routes.setRoute_url("https://amtrak.com");
            routesList.add(routes);

            Mockito.when(csvUtil.csvToObject(any(FileInputStream.class),
                any(Routes.class))).thenReturn(routesList);

            Assertions.assertDoesNotThrow(() -> 
                amtrakRoutesHandler.loadRoutesFromFiles());
        }

        @Test
        public void loadRoutesFromFiles_NothingInList() {
            List<ICVMapable> routesList = new ArrayList<>();

            Mockito.when(csvUtil.csvToObject(any(FileInputStream.class),
                any(Routes.class))).thenReturn(routesList);

            Assertions.assertDoesNotThrow(() -> 
                amtrakRoutesHandler.loadRoutesFromFiles());
        }

        @Test
        public void loadRoutesFromFiles_ThrowsFileNotFound() {
            Mockito.when(csvUtil.csvToObject(any(FileInputStream.class),
                any(Routes.class)))
                .thenThrow(new RuntimeException("I'm a runtime exception!"));

            Assertions.assertThrows(RuntimeException.class, 
                () -> amtrakRoutesHandler.loadRoutesFromFiles(),
                "Didn't throw the exception as expected");
        }
    }

    @Nested
    @DisplayName("Load Routes From Files")
    class FindRouteByRoutesId {
        private List<ICVMapable> routesList;

        @BeforeEach
        public void setUp() {
            Mockito.when(amtrakProperties.getDataDirectory())
                .thenReturn("./data");
        }

        @Test
        public void findRouteByRoutesId() {
            routesList = new ArrayList<>();
            Routes routes = new Routes();
            routes.setAgency_id(123456);
            routes.setRoute_color("FFFFFF");
            routes.setRoute_text_color("FFFFFF");
            routes.setRoute_id(654321);
            routes.setRoute_long_name("Acela Express");
            routes.setRoute_short_name("Acela");
            routes.setRoute_type("2");
            routes.setRoute_url("https://amtrak.com");
            routesList.add(routes);

            Mockito.when(csvUtil.csvToObject(any(FileInputStream.class),
                any(Routes.class))).thenReturn(routesList);

            Assertions.assertDoesNotThrow(() -> 
                amtrakRoutesHandler.loadRoutesFromFiles());
            Assertions.assertEquals(routes, 
                amtrakRoutesHandler.findRouteByRoutesId(654321));
        }

        @Test
        public void findRouteByRoutesId_NothingInList() {
            routesList = new ArrayList<>();
            Routes routes = null;
            Mockito.when(csvUtil.csvToObject(any(FileInputStream.class),
                any(Routes.class))).thenReturn(routesList);

            Assertions.assertDoesNotThrow(() -> 
                amtrakRoutesHandler.loadRoutesFromFiles());
            Assertions.assertEquals(routes, 
                amtrakRoutesHandler.findRouteByRoutesId(654321));
        }

        @Test
        public void findRouteByRoutesId_NotFound() {
            routesList = new ArrayList<>();
            Routes routes = new Routes();
            routes.setAgency_id(123456);
            routes.setRoute_color("FFFFFF");
            routes.setRoute_text_color("FFFFFF");
            routes.setRoute_id(000000);
            routes.setRoute_long_name("Acela Express");
            routes.setRoute_short_name("Acela");
            routes.setRoute_type("2");
            routes.setRoute_url("https://amtrak.com");
            routesList.add(routes);
            Mockito.when(csvUtil.csvToObject(any(FileInputStream.class),
                any(Routes.class))).thenReturn(routesList);

            Assertions.assertDoesNotThrow(() -> 
                amtrakRoutesHandler.loadRoutesFromFiles());
            Assertions.assertEquals(null, 
                amtrakRoutesHandler.findRouteByRoutesId(654321));
        }
    }
    
}
