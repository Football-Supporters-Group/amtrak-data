package com.wolginm.amtrak.data.properties;

import lombok.Data;
import org.springframework.retry.annotation.Backoff;

/**
 * Retry Properties Container.
 */
@Data
public class RetryableProperties {

    /**
     * How many retires to attempt.
     */
    private Integer maxRetryCount = 3;
    /**
     * The multiplier to apply to the default delay per retry count.
     * min((delay)*(multiplier*retryCount), maxDelay)
     */
    private Double multiplier = 2.0;
    /**
     * The base delay to use per retry count.
     * min((delay)*(multiplier*retryCount), maxDelay)
     */
    private Long delay = 1000L;
    /**
     * The maximum delay to apply to cap the calculated delay per retry count.
     * min((delay)*(multiplier*retryCount), maxDelay)
     */
    private Long maxDelay = 30000L;
}
