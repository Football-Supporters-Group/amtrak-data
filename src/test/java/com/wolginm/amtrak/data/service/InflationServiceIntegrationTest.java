package com.wolginm.amtrak.data.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wolginm.amtrak.data.configuration.TemporaryDirectoryConfiguration;
import com.wolginm.amtrak.data.properties.AmtrakProperties;
import com.wolginm.amtrak.data.properties.GtfsProperties;
import com.wolginm.amtrak.data.util.AmtrakFileNameToObjectUtil;
import com.wolginm.amtrak.data.util.ObjectsUtil;
import com.wolginmark.amtrak.data.models.Agency;
import com.wolginmark.amtrak.data.models.AmtrakObject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.NotDirectoryException;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
class InflationServiceIntegrationTest {

    private final ClassLoader classLoader = getClass().getClassLoader();
    private final String[] fileNames = {"unzip/agency.txt"};
    private final ObjectsUtil objectsUtil = new ObjectsUtil(new ObjectMapper());

    private final AmtrakFileNameToObjectUtil amtrakFileNameToObjectUtil = new AmtrakFileNameToObjectUtil();
    private final AmtrakProperties amtrakProperties = new AmtrakProperties();
    private InflationService inflationService;

    public InflationServiceIntegrationTest() throws IOException {
        this.amtrakProperties.setGtfs(new GtfsProperties());
        this.amtrakProperties.getGtfs().setDataDirectory(classLoader.getResource("unzip").getPath());
        this.inflationService = new InflationService(this.objectsUtil, this.amtrakFileNameToObjectUtil, this.amtrakProperties, new TemporaryDirectoryConfiguration().getTemporaryDirectory());
        ReflectionTestUtils.setField(inflationService, "tmpDataDir", Path.of(this.amtrakProperties.getGtfs().getDataDirectory()));
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

    @Test
    public <T extends AmtrakObject> void inflateAllAmtrakObjects() throws NotDirectoryException {
        Map<Class<T>, List<T>> actual;
        actual = this.inflationService.inflateAllAmtrakObjects();
        Assertions.assertEquals(8, actual.size());
    }

    @Test
    public <T extends AmtrakObject> void inflateAllAmtrakObjects_NotADirectory() {
        ReflectionTestUtils.setField(this.inflationService, "tmpDataDir", Path.of("unzip"));
        Assertions.assertThrows(NotDirectoryException.class, () -> this.inflationService.inflateAllAmtrakObjects());
    }

}