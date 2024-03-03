package com.markwolgin.amtrak.data.properties;

import lombok.Data;

/**
 * A container for holding web client properties.
 */
@Data
public class WebClientCustomProperties {

    /**
     * A timeout variable for the web client.  Default timeout is 60000 (60 sec).
     */
    private Integer connectionTimeoutInMilliseconds = 60000;

    /**
     * A timeout variable for the web client.  Default timeout is 60000 (60 sec).
     */
    private Integer responseTimeoutInMilliseconds = 60000;

    /**
     * A timeout variable for the web client.  Default timeout is 60000 (60 sec).
     */
    private Integer readTimeoutInMilliseconds = 60000;

    /**
     * A timeout variable for the web client.  Default timeout is 60000 (60 sec).
     */
    private Integer writeTimeoutInMilliseconds = 60000;
}
