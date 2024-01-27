package com.wolginm.amtrak.data.client;

import com.wolginm.amtrak.data.properties.GtfsProperties;
import com.wolginm.amtrak.data.util.FileUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.retry.RetryContext;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.retry.support.RetrySynchronizationManager;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Flux;

import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

@Slf4j
@Component
public class AmtrakDataClient extends ClientBase {

    private final GtfsProperties gtfsProperties;
    private final WebClient webClient;
    private final FileUtil fileUtil;

    public AmtrakDataClient(final GtfsProperties gtfsProperties,
                            final FileUtil fileUtil,
                            @Qualifier("AmtrakDataWebClient") final WebClient webClient) {
        this.gtfsProperties = gtfsProperties;
        this.webClient = webClient;
        this.fileUtil = fileUtil;
        log.info("AMTK-3100: Created the Amtrak Data Client");
    }

    @Retryable(maxAttemptsExpression = "${amtrak.gtfs.retry.maxRetryCount}",
                backoff = @Backoff(
                        delayExpression = "${amtrak.gtfs.retry.delay}",
                        maxDelayExpression = "${amtrak.gtfs.retry.maxDelay}",
                        multiplierExpression = "${amtrak.gtfs.retry.multiplier}"
                ))
    public String retrieveGtfsPayload() {
        RetryContext retryContext = RetrySynchronizationManager.getContext();
        log.info("AMTK-3100: [{}/{}] In AmtrakDataClient.retrieveGtfsPayload");

        try {
            Flux<DataBuffer> downloadedMono = webClient
                    .get()
                    .uri(this.gtfsProperties.getPath())
                    .retrieve()
                    .bodyToFlux(DataBuffer.class);
            log.info("AMTK-3101: The requested file at [{}:{}{}] has been queried and returned response non 4xx/5xx",
                    this.gtfsProperties.getSchema(),
                    this.gtfsProperties.getHost(),
                    this.gtfsProperties.getPath());

            Path placeToStoreFile = this.fileUtil.resolvePath(this.gtfsProperties.getTempFile());
            this.fileUtil.prepFoldersForFile(placeToStoreFile);

            DataBufferUtils.write(downloadedMono,
                    placeToStoreFile,
                    StandardOpenOption.CREATE).block();
            log.info("AMTK-3100: Amtrak GTFS data file has been saved to [{}]", placeToStoreFile);
        } catch (WebClientResponseException exception) {
            int statusCode = exception.getStatusCode().value();
            log.error("AMTK-3199: Received status code [{}] from [{}:{}{}]",
                    statusCode,
                    this.gtfsProperties.getSchema(),
                    this.gtfsProperties.getHost(),
                    this.gtfsProperties.getPath());
            if (HttpStatus.TOO_MANY_REQUESTS.value() == statusCode
                    || HttpStatus.GATEWAY_TIMEOUT.value() == statusCode
                    || HttpStatus.INTERNAL_SERVER_ERROR.value() == statusCode) {
                //todo;
            }
        }

        log.info("AMTK-3100: Exiting AmtrakDataClient.retrieveGtfsPayload");
    }
}
