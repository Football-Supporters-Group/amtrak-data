package com.wolginm.amtrak.data.client;

import com.wolginm.amtrak.data.exception.NonRetryableException;
import com.wolginm.amtrak.data.exception.RetryableException;
import com.wolginm.amtrak.data.properties.GtfsProperties;
import com.wolginm.amtrak.data.util.FileUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.retry.RetryContext;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.retry.support.RetrySynchronizationManager;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Flux;

import java.nio.file.Path;

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

    @Retryable(label = "retrieveGtfsPayload.retry",
            retryFor = {RetryableException.class},
            noRetryFor = {NonRetryableException.class},
            maxAttemptsExpression = "${amtrak.gtfs.retry.maxRetryCount}",
                backoff = @Backoff(
                        delayExpression = "${amtrak.gtfs.retry.delay}",
                        maxDelayExpression = "${amtrak.gtfs.retry.maxDelay}",
                        multiplierExpression = "${amtrak.gtfs.retry.multiplier}"
                ))
    public Path retrieveGtfsPayload() {
        RetryContext retryContext = RetrySynchronizationManager.getContext();
        int retry = retryContext.getRetryCount() + 1;
        Path placeToStoreFile;
        log.info("AMTK-3100: [{}/{}] In AmtrakDataClient.retrieveGtfsPayload",
                retry, gtfsProperties.getRetry().getMaxRetryCount());

        try {
            Flux<DataBuffer> downloadedMono = webClient
                    .get()
                    .uri(this.gtfsProperties.getPath())
                    .retrieve()
                    .bodyToFlux(DataBuffer.class);
            log.info("AMTK-3101: [{}/{}] The requested file at [{}:{}{}] has been queried and returned response non 4xx/5xx",
                    retry,
                    this.gtfsProperties.getRetry().getMaxRetryCount(),
                    this.gtfsProperties.getSchema(),
                    this.gtfsProperties.getHost(),
                    this.gtfsProperties.getPath());

            placeToStoreFile = this.fileUtil.prepFoldersForFile("zip");

            this.fileUtil.dataBufferUtilWrite(downloadedMono, placeToStoreFile);
            log.info("AMTK-3100: [{}/{}] Amtrak GTFS data file has been saved to [{}]",
                    retry,
                    this.gtfsProperties.getRetry().getMaxRetryCount(),
                    placeToStoreFile);
        } catch (WebClientResponseException exception) {
            int statusCode = exception.getStatusCode().value();
            log.error("AMTK-3190: [{}/{}] Received status code [{}] from [{}:{}{}]",
                    retry,
                    this.gtfsProperties.getRetry().getMaxRetryCount(),
                    statusCode,
                    this.gtfsProperties.getSchema(),
                    this.gtfsProperties.getHost(),
                    this.gtfsProperties.getPath());
            if (HttpStatus.TOO_MANY_REQUESTS.value() == statusCode
                    || HttpStatus.GATEWAY_TIMEOUT.value() == statusCode
                    || HttpStatus.INTERNAL_SERVER_ERROR.value() == statusCode) {
                log.error("AMTK-3198: [{}/{}] Retryable exception",
                        retry,
                        this.gtfsProperties.getRetry().getMaxRetryCount());
                throw new RetryableException(exception.getStatusText(), exception.getStatusCode(), exception);
            } else {
                log.error("AMTK-3199: [{}/{}] Non retryable exception",
                        retry,
                        this.gtfsProperties.getRetry().getMaxRetryCount());
                throw new NonRetryableException();
            }
        }

        log.info("AMTK-3100: [{}/{}] Exiting AmtrakDataClient.retrieveGtfsPayload",
                retry,
                this.gtfsProperties.getRetry().getMaxRetryCount());
        return placeToStoreFile;
    }

    @Recover()
    public Path retrieveGtfsPayload(final RetryableException retryableException) {
        int retry = RetrySynchronizationManager.getContext().getRetryCount();
        log.info("AMTK-3199: [{}/{}] All retries have failed to get the GTFS Payload.  Source [{}, {}]",
                retry,
                this.gtfsProperties.getRetry().getMaxRetryCount(),
                retryableException.getReason(), retryableException.getHttpStatusCode());
        return null;
    }
}
