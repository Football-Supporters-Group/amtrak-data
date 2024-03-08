package com.markwolgin.amtrak.data.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.markwolgin.amtrak.data.configuration.TemporaryDirectoryConfiguration;
import com.markwolgin.amtrak.data.util.*;
import com.markwolgin.amtrak.data.client.AmtrakDataClient;
import com.markwolgin.amtrak.data.properties.AmtrakProperties;
import com.markwolgin.amtrak.data.properties.GtfsProperties;
import com.markwolgin.amtrak.data.properties.RetryableProperties;
import com.markwolgin.amtrak.data.models.ConsolidatedResponseObject;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.retry.RetryContext;
import org.springframework.retry.support.RetrySynchronizationManager;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.util.Objects;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;


//TODO: Need to write integration tests to check the full flow, soup to nuts.
@Slf4j
@ExtendWith(MockitoExtension.class)
class DataManagementServiceIntegrationTest {

    private final ClassLoader classLoader = this.getClass().getClassLoader();

    private final String mockMetadata = Objects.requireNonNull(this.classLoader.getResource("metadata/route_stop_order.txt")).getPath();
    private final String zipFile = "zip/gtfs_slim.zip";
    private final String unZip = "unzip/tmp";

    private DataManagementService dataManagementService;

    private AmtrakFileNameToObjectUtil amtrakFileNameToObjectUtil;
    private InflationService inflationService;
    private AmtrakDataClient amtrakDataClient;
    private AmtrakProperties amtrakProperties;
    private DataMappingUtil dataMappingUtil;
    private final RetryContext retryContext = mock(RetryContext.class);
    private ObjectsUtil objectsUtil;
    private Path temporaryDirectory;
    private WebClient webClient;
    private FileUtil fileUtil;
    private ZipUtil zipUtil;

    @BeforeEach
    void setUp() throws IOException {
        RetrySynchronizationManager.register(this.retryContext);
        Mockito.when(this.retryContext.getRetryCount()).thenReturn(0);
        this.amtrakFileNameToObjectUtil = new AmtrakFileNameToObjectUtil();

        this.amtrakProperties = new AmtrakProperties();
        this.amtrakProperties.setRoute_metadata(this.mockMetadata);
        this.amtrakProperties.setGtfs(new GtfsProperties());
        this.amtrakProperties.getGtfs().setDataDirectory(this.unZip);
        this.amtrakProperties.getGtfs().setHost("localhost");
        this.amtrakProperties.getGtfs().setSchema("https");
        this.amtrakProperties.getGtfs().setRetry(new RetryableProperties());
        this.amtrakProperties.getGtfs().getRetry().setDelay(10L);
        this.amtrakProperties.getGtfs().getRetry().setMultiplier(1.2);
        this.amtrakProperties.getGtfs().getRetry().setMaxRetryCount(10);
        this.amtrakProperties.getGtfs().getRetry().setMaxDelay(1010101L);


        this.webClient = mock(WebClient.class);
        this.temporaryDirectory = new TemporaryDirectoryConfiguration().getTemporaryDirectory();
        this.fileUtil = new FileUtil(
                this.temporaryDirectory);

        this.amtrakDataClient = new AmtrakDataClient(
                this.amtrakProperties.getGtfs(),
                this.fileUtil,
                this.webClient);

        this.dataMappingUtil = new DataMappingUtil();
        this.objectsUtil = new ObjectsUtil(new ObjectMapper());
        this.zipUtil = new ZipUtil();

        this.inflationService = new InflationService(
                this.objectsUtil,
                this.amtrakFileNameToObjectUtil,
                this.amtrakProperties,
                this.temporaryDirectory);

        this.dataManagementService = new DataManagementService(
                this.inflationService,
                this.amtrakDataClient,
                this.fileUtil,
                this.zipUtil,
                this.dataMappingUtil,
                this.amtrakProperties,
                this.temporaryDirectory
        );
    }


    @Test
    void LoadInAllData_Pass() throws URISyntaxException, IOException {

        Path dataBufferSource = Path.of(this.getClass().getClassLoader().getResource(zipFile).toURI());
        DataBufferFactory dataBufferFactory = new DefaultDataBufferFactory();
        Flux<DataBuffer> flux = DataBufferUtils.read(dataBufferSource, dataBufferFactory, 409600);

        Path destination = Path.of(this.fileUtil.getTempDirectory().toString(), "dataBufferTest");
        destination.toFile().deleteOnExit();

        WebClient.RequestHeadersUriSpec requestHeadersUriSpec = mock(WebClient.RequestHeadersUriSpec.class);
        WebClient.RequestHeadersSpec requestHeadersSpec = mock(WebClient.RequestHeadersSpec.class);
        WebClient.ResponseSpec responseSpec = mock(WebClient.ResponseSpec.class);
        Mockito.when(webClient.get()).thenReturn(requestHeadersUriSpec);
        Mockito.when(requestHeadersUriSpec.uri(eq((this.amtrakProperties.getGtfs().getPath())))).thenReturn(requestHeadersSpec);
        Mockito.when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        Mockito.when(responseSpec.bodyToFlux(eq(DataBuffer.class))).thenReturn(flux);

        ConsolidatedResponseObject consolidatedResponseObject = this.dataManagementService.buildConsolidatedResponseObject();

        Assertions.assertNotNull(consolidatedResponseObject);
        Assertions.assertEquals(consolidatedResponseObject.getRequestedConsolidatedRoutes().get().size(), consolidatedResponseObject.getRequestedRouteIds().size());

        Path.of(this.temporaryDirectory.toString(), "zip/data.zip").toFile().deleteOnExit();
        Path.of(this.temporaryDirectory.toString(), "zip").toFile().deleteOnExit();
        Path.of(this.temporaryDirectory.toString(), unZip).toFile().deleteOnExit();
        Path.of(this.temporaryDirectory.toString(), "unzip").toFile().deleteOnExit();
        for (File file : Path.of(this.temporaryDirectory.toString(), unZip).toFile().listFiles()) {
            file.deleteOnExit();
        }

        log.info("Tearing down temp files.");
        this.fileUtil.tearDownRecursive(this.temporaryDirectory.toFile());
    }

    @Test
    void LoadInAllData_NoData() throws URISyntaxException, IOException {

        Path dataBufferSource = Path.of(this.getClass().getClassLoader().getResource(zipFile).toURI());
        DataBufferFactory dataBufferFactory = new DefaultDataBufferFactory();
        Flux<DataBuffer> flux = DataBufferUtils.read(dataBufferSource, dataBufferFactory, 409600);

        Path destination = Path.of(this.fileUtil.getTempDirectory().toString(), "dataBufferTest");
        destination.toFile().deleteOnExit();

        WebClient.RequestHeadersUriSpec requestHeadersUriSpec = mock(WebClient.RequestHeadersUriSpec.class);
        WebClient.RequestHeadersSpec requestHeadersSpec = mock(WebClient.RequestHeadersSpec.class);
        WebClient.ResponseSpec responseSpec = mock(WebClient.ResponseSpec.class);
        Mockito.when(webClient.get()).thenReturn(requestHeadersUriSpec);
        Mockito.when(requestHeadersUriSpec.uri(eq((this.amtrakProperties.getGtfs().getPath())))).thenReturn(requestHeadersSpec);
        Mockito.when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        Mockito.when(responseSpec.bodyToFlux(eq(DataBuffer.class))).thenReturn(flux);

        ConsolidatedResponseObject consolidatedResponseObject = this.dataManagementService.buildConsolidatedResponseObject("-1");

        Assertions.assertNotNull(consolidatedResponseObject);
        Assertions.assertEquals(consolidatedResponseObject.getRequestedConsolidatedRoutes().get().size(), consolidatedResponseObject.getRequestedRouteIds().size());

        Path.of(this.temporaryDirectory.toString(), "zip/data.zip").toFile().deleteOnExit();
        Path.of(this.temporaryDirectory.toString(), "zip").toFile().deleteOnExit();
        Path.of(this.temporaryDirectory.toString(), unZip).toFile().deleteOnExit();
        Path.of(this.temporaryDirectory.toString(), "unzip").toFile().deleteOnExit();
        for (File file : Path.of(this.temporaryDirectory.toString(), unZip).toFile().listFiles()) {
            file.deleteOnExit();
        }

        log.info("Tearing down temp files.");
        this.fileUtil.tearDownRecursive(this.temporaryDirectory.toFile());
    }

    @Test
    void LoadInAllData_OneData() throws URISyntaxException, IOException {

        Path dataBufferSource = Path.of(this.getClass().getClassLoader().getResource(zipFile).toURI());
        DataBufferFactory dataBufferFactory = new DefaultDataBufferFactory();
        Flux<DataBuffer> flux = DataBufferUtils.read(dataBufferSource, dataBufferFactory, 409600);

        Path destination = Path.of(this.fileUtil.getTempDirectory().toString(), "dataBufferTest");
        destination.toFile().deleteOnExit();

        WebClient.RequestHeadersUriSpec requestHeadersUriSpec = mock(WebClient.RequestHeadersUriSpec.class);
        WebClient.RequestHeadersSpec requestHeadersSpec = mock(WebClient.RequestHeadersSpec.class);
        WebClient.ResponseSpec responseSpec = mock(WebClient.ResponseSpec.class);
        Mockito.when(webClient.get()).thenReturn(requestHeadersUriSpec);
        Mockito.when(requestHeadersUriSpec.uri(eq((this.amtrakProperties.getGtfs().getPath())))).thenReturn(requestHeadersSpec);
        Mockito.when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        Mockito.when(responseSpec.bodyToFlux(eq(DataBuffer.class))).thenReturn(flux);

        ConsolidatedResponseObject consolidatedResponseObject = this.dataManagementService.buildConsolidatedResponseObject("94");

        Assertions.assertNotNull(consolidatedResponseObject);
        Assertions.assertEquals(consolidatedResponseObject.getRequestedConsolidatedRoutes().get().size(), consolidatedResponseObject.getRequestedRouteIds().size());

        Path.of(this.temporaryDirectory.toString(), "zip/data.zip").toFile().deleteOnExit();
        Path.of(this.temporaryDirectory.toString(), "zip").toFile().deleteOnExit();
        Path.of(this.temporaryDirectory.toString(), unZip).toFile().deleteOnExit();
        Path.of(this.temporaryDirectory.toString(), "unzip").toFile().deleteOnExit();
        for (File file : Path.of(this.temporaryDirectory.toString(), unZip).toFile().listFiles()) {
            file.deleteOnExit();
        }

        log.info("Tearing down temp files.");
        this.fileUtil.tearDownRecursive(this.temporaryDirectory.toFile());
    }
}