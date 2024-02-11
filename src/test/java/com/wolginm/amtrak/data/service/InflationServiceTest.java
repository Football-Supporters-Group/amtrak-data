package com.wolginm.amtrak.data.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wolginm.amtrak.data.properties.AmtrakProperties;
import com.wolginm.amtrak.data.properties.GtfsProperties;
import com.wolginm.amtrak.data.util.AmtrakFileNameToObjectUtil;
import com.wolginm.amtrak.data.util.ObjectsUtil;
import com.wolginmark.amtrak.data.models.Agency;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.util.LinkedList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
class InflationServiceTest {

    private final ClassLoader classLoader = getClass().getClassLoader();
    private final String[] fileNames = {"unzip/agency.txt",
            "unzip/calendar.txt",
            "unzip/feed_info.txt",
            "unzip/routes.txt",
            "unzip/shapes.txt",
            "unzip/stop_times.txt",
            "unzip/stops.txt",
            "unzip/transfers.txt",
            "unzip/trips.txt"};

    private final GtfsProperties gtfsProperties = new GtfsProperties();

    @Mock
    private ObjectsUtil objectsUtil;

    @Mock
    private AmtrakFileNameToObjectUtil amtrakFileNameToObjectUtil;

    private AmtrakProperties amtrakProperties;

    private InflationService inflationService;

    @BeforeEach
    void setUp() {
        this.amtrakProperties = new AmtrakProperties();
        this.amtrakProperties.setGtfs(this.gtfsProperties);
        this.gtfsProperties.setDataDirectory("unzip");
        MockitoAnnotations.openMocks(this);
        this.inflationService = new InflationService(objectsUtil, amtrakFileNameToObjectUtil, amtrakProperties);
    }

    @Test
    public void inflateAmtrakObject() throws URISyntaxException, FileNotFoundException {
        URL url = classLoader.getResource(fileNames[0]);
        Path inflatedObject = Path.of(url.toURI());
        Agency agency = new Agency(186, "Adirondack Trailways",
                URI.create("http://www.amtrak.com"), "America/New_York", "en");

        List<Agency> actual;
        Mockito.when(this.objectsUtil.mapToObject(anyMap(), eq(Agency.class)))
                .thenReturn(agency);
        actual = this.inflationService.inflateAmtrakObject(inflatedObject, Agency.class);

        Assertions.assertNotNull(actual);
        Assertions.assertEquals(actual.size(), 42);
        Assertions.assertInstanceOf(Agency.class, actual.get(0));
        Assertions.assertEquals(186, actual.get(0).getAgencyId());
        Assertions.assertEquals("Adirondack Trailways", actual.get(0).getAgencyName());
        Assertions.assertEquals("http://www.amtrak.com", actual.get(0).getAgencyUrl().toString());
        Assertions.assertEquals("America/New_York", actual.get(0).getAgencyTimezone());
        Assertions.assertEquals("en", actual.get(0).getAgencyLang());
        Mockito.verify(this.objectsUtil, times(42)).mapToObject(anyMap(), eq(Agency.class));
    }

    @Test
    public void inflateAmtrakObject_Fail_IllegalArgument() throws URISyntaxException, FileNotFoundException {
        URL url = classLoader.getResource(fileNames[0]);
        Path inflatedObject = Path.of(url.toURI());

        List<Agency> actual;
        Mockito.when(this.objectsUtil.mapToObject(anyMap(), eq(Agency.class)))
                .thenThrow(new IllegalArgumentException());
        actual = this.inflationService.inflateAmtrakObject(inflatedObject, Agency.class);

        Assertions.assertNull(actual);
        Mockito.verify(this.objectsUtil, times(1)).mapToObject(anyMap(), eq(Agency.class));
    }


}