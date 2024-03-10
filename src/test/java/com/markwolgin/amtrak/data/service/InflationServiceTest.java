package com.markwolgin.amtrak.data.service;

import com.markwolgin.amtrak.data.configuration.TemporaryDirectoryConfiguration;
import com.markwolgin.amtrak.data.util.AmtrakFileNameToObjectUtil;
import com.markwolgin.amtrak.data.properties.AmtrakProperties;
import com.markwolgin.amtrak.data.properties.GtfsProperties;
import com.markwolgin.amtrak.data.util.ObjectsUtil;
import com.markwolgin.amtrak.data.models.Agency;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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
import java.util.*;

import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.eq;
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
    void setUp() throws IOException {
        this.amtrakProperties = new AmtrakProperties();
        this.amtrakProperties.setRoute_metadata("mock_metadata/route_stop_order.txt");
        this.amtrakProperties.setGtfs(this.gtfsProperties);
        this.gtfsProperties.setDataDirectory("unzip");
        MockitoAnnotations.openMocks(this);
        this.inflationService = new InflationService(objectsUtil, amtrakFileNameToObjectUtil, amtrakProperties, new TemporaryDirectoryConfiguration().getTemporaryDirectory());
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

    @Test
    public void inflateRouteOrderMetadata() throws FileNotFoundException {
        Map<String, LinkedHashSet<String>> actual, expected;
        expected = new HashMap<>(){{
           put("1", new LinkedHashSet<>());
           put("2", new LinkedHashSet<>());
        }};
        expected.get("1").add("NYP");
        expected.get("1").add("PHL");
        expected.get("1").add("PAO");
        expected.get("1").add("HAR");
        expected.get("2").add("NYP");
        expected.get("2").add("CHI");
        expected.get("2").add("SEA");
        expected.get("2").add("PDX");

        actual = this.inflationService.inflateRouteOrderMetadata();

        Iterator<String> expectItr = expected.get("1").iterator();
        Iterator<String> actualItr = actual.get("1").iterator();
        while (expectItr.hasNext()) {
            Assertions.assertEquals(expectItr.next(), actualItr.next());
        }
    }

    @Test
    public void inflateRouteOrderMetadata_IncorrectOrder() throws FileNotFoundException {
        Map<String, LinkedHashSet<String>> actual, expected;
        expected = new HashMap<>(){{
            put("1", new LinkedHashSet<>());
            put("2", new LinkedHashSet<>());
        }};
        expected.get("1").add("NYP");
        expected.get("1").add("PHL");
        expected.get("1").add("PAO");
        expected.get("1").add("HAR");
        expected.get("2").add("NYP");
        expected.get("2").add("CHI");
        expected.get("2").add("PDX");
        expected.get("2").add("SEA");

        actual = this.inflationService.inflateRouteOrderMetadata();

        Iterator<String> expectItr = expected.get("2").iterator();
        Iterator<String> actualItr = actual.get("2").iterator();
        int count = 0;
        while (expectItr.hasNext() && count < 2) {
            count ++;
            Assertions.assertEquals(expectItr.next(), actualItr.next());
        }
        Assertions.assertNotEquals(expectItr.next(), actualItr.next());
        Assertions.assertNotEquals(expectItr.next(), actualItr.next());
    }


}