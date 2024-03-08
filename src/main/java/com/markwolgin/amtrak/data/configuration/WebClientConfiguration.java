package com.markwolgin.amtrak.data.configuration;

import com.markwolgin.amtrak.data.properties.GtfsProperties;
import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;
import java.util.Collections;
import java.util.concurrent.TimeUnit;

@Slf4j
@Configuration
public class WebClientConfiguration {

    @Bean("AmtrakDataWebClient")
    public WebClient amtrakDataWebClient(final GtfsProperties gtfsProperties) {
        final String path = "%s:%s".formatted(gtfsProperties.getSchema(), gtfsProperties.getHost());
        log.info("AMTK-3210: Initializing Amtrak Data Web Client with base url [{}]", path);
        log.info("AMTK-3210:    with ConnectionTimeout[{}ms] ResponseTimeout[{}ms]",
                gtfsProperties.getWebClient().getConnectionTimeoutInMilliseconds(),
                gtfsProperties.getWebClient().getResponseTimeoutInMilliseconds());
        log.info("AMTK-3210:    with ReadTimeout[{}ms] WriteTimeout[{}ms]",
                gtfsProperties.getWebClient().getReadTimeoutInMilliseconds(),
                gtfsProperties.getWebClient().getWriteTimeoutInMilliseconds());

        return WebClient.builder()
                .baseUrl(path)
                .clientConnector(new ReactorClientHttpConnector(HttpClient.create()
                        .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, gtfsProperties.getWebClient().getConnectionTimeoutInMilliseconds())
                        .responseTimeout(Duration.ofMillis(gtfsProperties.getWebClient().getResponseTimeoutInMilliseconds()))
                        .doOnConnected(connection ->
                                connection.addHandlerLast(new ReadTimeoutHandler(gtfsProperties.getWebClient().getReadTimeoutInMilliseconds(), TimeUnit.MILLISECONDS))
                                        .addHandlerLast(new WriteTimeoutHandler(gtfsProperties.getWebClient().getWriteTimeoutInMilliseconds(), TimeUnit.MILLISECONDS)))))
                .defaultCookie("amtakDataVersion", this.getClass().getPackage().getImplementationVersion())
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.ALL_VALUE)
                .defaultUriVariables(Collections.singletonMap("url", path))
                .build();
    }

}
