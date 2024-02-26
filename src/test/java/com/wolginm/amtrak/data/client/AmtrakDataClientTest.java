package com.wolginm.amtrak.data.client;

import com.wolginm.amtrak.data.exception.NonRetryableException;
import com.wolginm.amtrak.data.exception.RetryableException;
import com.wolginm.amtrak.data.properties.GtfsProperties;
import com.wolginm.amtrak.data.properties.RetryableProperties;
import com.wolginm.amtrak.data.util.FileUtil;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatusCode;
import org.springframework.retry.RetryContext;
import org.springframework.retry.support.RetrySynchronizationManager;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Flux;

import java.nio.file.FileSystems;
import java.nio.file.Path;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
class AmtrakDataClientTest {

    @InjectMocks
    private AmtrakDataClient amtrakDataClient;
    @Mock
    private GtfsProperties gtfsProperties;
    @Mock
    private WebClient webClient;
    @Mock
    private FileUtil fileUtil;
    @Mock
    private RetryContext retryContext;
    private RetryableProperties retryableProperties;
    private final String pathPart = "/path-part";
    private final String hostPart = "markwolgin.com/api/amtrak/data";
    private final String schemaPart = "https";
    private final String tempPath = "zip";

    @BeforeEach
    public void setUp() {
        RetrySynchronizationManager.register(retryContext);
        Mockito.when(retryContext.getRetryCount()).thenReturn(0);

        retryableProperties = new RetryableProperties();
        retryableProperties.setMaxDelay(100L);
        retryableProperties.setDelay(1L);
        retryableProperties.setMultiplier(2.0);
        retryableProperties.setMaxRetryCount(10);

        Mockito.when(gtfsProperties.getRetry()).thenReturn(retryableProperties);
    }

    @Nested
    @DisplayName("Main Body Tests")
    class RetrieveGTFS {

        @BeforeEach
        void setUp() {
            Mockito.when(gtfsProperties.getPath()).thenReturn(pathPart);
            Mockito.when(gtfsProperties.getSchema()).thenReturn(schemaPart);
            Mockito.when(gtfsProperties.getHost()).thenReturn(hostPart);
        }

        @Test
        void retrieveGtfsPayload_pass() {
            Path actual, expected;
            expected = FileSystems.getDefault()
                    .getPath(tempPath)
                    .normalize()
                    .toAbsolutePath();

            WebClient.RequestHeadersUriSpec requestHeadersUriSpec = mock(WebClient.RequestHeadersUriSpec.class);
            WebClient.RequestHeadersSpec requestHeadersSpec = mock(WebClient.RequestHeadersSpec.class);
            WebClient.ResponseSpec responseSpec = mock(WebClient.ResponseSpec.class);
            Flux<DataBuffer> dataBufferFlux = mock(Flux.class);
            Mockito.when(webClient.get()).thenReturn(requestHeadersUriSpec);
            Mockito.when(requestHeadersUriSpec.uri(eq((gtfsProperties.getPath())))).thenReturn(requestHeadersSpec);
            Mockito.when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
            Mockito.when(responseSpec.bodyToFlux(eq(DataBuffer.class))).thenReturn(dataBufferFlux);

            Mockito.when(fileUtil.prepFoldersForFile(tempPath)).thenReturn(expected);
            Mockito.doNothing().when(fileUtil).dataBufferUtilWrite(any(Flux.class), any(Path.class));

            actual = amtrakDataClient.retrieveGtfsPayload();
            Assertions.assertEquals(expected.toString().concat("/data.zip"), actual.toString());
        }

        @Test
        void retrieveGtfsPayload_fail_400() {
            WebClient.RequestHeadersUriSpec requestHeadersUriSpec = mock(WebClient.RequestHeadersUriSpec.class);
            WebClient.RequestHeadersSpec requestHeadersSpec = mock(WebClient.RequestHeadersSpec.class);
            WebClientResponseException responseException = mock(WebClientResponseException.class);

            Mockito.when(webClient.get()).thenReturn(requestHeadersUriSpec);
            Mockito.when(requestHeadersUriSpec.uri(eq((gtfsProperties.getPath())))).thenReturn(requestHeadersSpec);
            Mockito.when(responseException.getStatusCode()).thenReturn(HttpStatusCode.valueOf(400));
            Mockito.when(requestHeadersSpec.retrieve()).thenThrow(responseException);

            Assertions.assertThrows(NonRetryableException.class, () -> amtrakDataClient.retrieveGtfsPayload());
        }

        @Test
        void retrieveGtfsPayload_fail_401() {
            WebClient.RequestHeadersUriSpec requestHeadersUriSpec = mock(WebClient.RequestHeadersUriSpec.class);
            WebClient.RequestHeadersSpec requestHeadersSpec = mock(WebClient.RequestHeadersSpec.class);
            WebClientResponseException responseException = mock(WebClientResponseException.class);

            Mockito.when(webClient.get()).thenReturn(requestHeadersUriSpec);
            Mockito.when(requestHeadersUriSpec.uri(eq((gtfsProperties.getPath())))).thenReturn(requestHeadersSpec);
            Mockito.when(responseException.getStatusCode()).thenReturn(HttpStatusCode.valueOf(401));
            Mockito.when(requestHeadersSpec.retrieve()).thenThrow(responseException);

            Assertions.assertThrows(NonRetryableException.class, () -> amtrakDataClient.retrieveGtfsPayload());
        }

        @Test
        void retrieveGtfsPayload_fail_429() {
            WebClient.RequestHeadersUriSpec requestHeadersUriSpec = mock(WebClient.RequestHeadersUriSpec.class);
            WebClient.RequestHeadersSpec requestHeadersSpec = mock(WebClient.RequestHeadersSpec.class);
            WebClientResponseException responseException = mock(WebClientResponseException.class);

            Mockito.when(webClient.get()).thenReturn(requestHeadersUriSpec);
            Mockito.when(requestHeadersUriSpec.uri(eq((gtfsProperties.getPath())))).thenReturn(requestHeadersSpec);
            Mockito.when(responseException.getStatusCode()).thenReturn(HttpStatusCode.valueOf(429));
            Mockito.when(requestHeadersSpec.retrieve()).thenThrow(responseException);

            Assertions.assertThrows(RetryableException.class, () -> amtrakDataClient.retrieveGtfsPayload());
        }

        @Test
        void retrieveGtfsPayload_fail_500() {
            WebClient.RequestHeadersUriSpec requestHeadersUriSpec = mock(WebClient.RequestHeadersUriSpec.class);
            WebClient.RequestHeadersSpec requestHeadersSpec = mock(WebClient.RequestHeadersSpec.class);
            WebClientResponseException responseException = mock(WebClientResponseException.class);

            Mockito.when(webClient.get()).thenReturn(requestHeadersUriSpec);
            Mockito.when(requestHeadersUriSpec.uri(eq((gtfsProperties.getPath())))).thenReturn(requestHeadersSpec);
            Mockito.when(responseException.getStatusCode()).thenReturn(HttpStatusCode.valueOf(500));
            Mockito.when(requestHeadersSpec.retrieve()).thenThrow(responseException);

            Assertions.assertThrows(RetryableException.class, () -> amtrakDataClient.retrieveGtfsPayload());
        }

        @Test
        void retrieveGtfsPayload_fail_504() {
            WebClient.RequestHeadersUriSpec requestHeadersUriSpec = mock(WebClient.RequestHeadersUriSpec.class);
            WebClient.RequestHeadersSpec requestHeadersSpec = mock(WebClient.RequestHeadersSpec.class);
            WebClientResponseException responseException = mock(WebClientResponseException.class);

            Mockito.when(webClient.get()).thenReturn(requestHeadersUriSpec);
            Mockito.when(requestHeadersUriSpec.uri(eq((gtfsProperties.getPath())))).thenReturn(requestHeadersSpec);
            Mockito.when(responseException.getStatusCode()).thenReturn(HttpStatusCode.valueOf(504));
            Mockito.when(requestHeadersSpec.retrieve()).thenThrow(responseException);

            Assertions.assertThrows(RetryableException.class, () -> amtrakDataClient.retrieveGtfsPayload());
        }
    }



    @Test
    void testRetrieveGtfsPayload() {
        RetryableException retryableException = new RetryableException("A good reason", HttpStatusCode.valueOf(418));
        Assertions.assertNull(amtrakDataClient.retrieveGtfsPayload(retryableException));

    }
}