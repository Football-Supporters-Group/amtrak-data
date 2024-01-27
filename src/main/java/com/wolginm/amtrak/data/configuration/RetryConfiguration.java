package com.wolginm.amtrak.data.configuration;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.annotation.EnableRetry;

@Slf4j
@Configuration
@EnableRetry
@ConditionalOnExpression("${amtrak.retry-enabled}")
public class RetryConfiguration {

    public RetryConfiguration() {
        log.info("AMTK-5100: Enabling Retry Logic for Amtrak Data Services");
    }

}
