package com.wolginm.amtrak.data.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wolginm.amtrak.data.util.ObjectsUtil;
import com.wolginmark.amtrak.data.models.Agency;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.FileNotFoundException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
class InflationServiceIntegrationTest {

    private final ClassLoader classLoader = getClass().getClassLoader();
    private final String[] fileNames = {"unzip/agency.txt"};
    private ObjectsUtil objectsUtil = new ObjectsUtil(new ObjectMapper());

    private InflationService inflationService;

    public InflationServiceIntegrationTest() {
        this.inflationService = new InflationService(this.objectsUtil);
    }

    @Test
    public void inflateAmtrakObject() throws URISyntaxException, FileNotFoundException {
        URL url = classLoader.getResource(fileNames[0]);
        Path inflatedObject = Path.of(url.toURI());
        Agency agency = new Agency(186, "Adirondack Trailways",
                URI.create("http://www.amtrak.com"), "America/New_York", "en");

        List<Agency> actual = this.inflationService.inflateAmtrakObject(inflatedObject, Agency.class);

        Assertions.assertNotNull(actual);
        Assertions.assertEquals(actual.size(), 42);
        Assertions.assertInstanceOf(Agency.class, actual.get(0));
        Assertions.assertEquals(186, actual.get(0).getAgencyId());
        Assertions.assertEquals("Adirondack Trailways", actual.get(0).getAgencyName());
        Assertions.assertEquals("http://www.amtrak.com", actual.get(0).getAgencyUrl().toString());
        Assertions.assertEquals("America/New_York", actual.get(0).getAgencyTimezone());
        Assertions.assertEquals("en", actual.get(0).getAgencyLang());
    }

}