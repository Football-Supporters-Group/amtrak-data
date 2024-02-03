package com.wolginm.amtrak.data.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

public class RetryableException extends RuntimeException {

    public final String reason;
    public final HttpStatusCode httpStatusCode;

    public RetryableException(final String reason, final HttpStatusCode httpStatusCode) {
        super();
        this.reason = reason;
        this.httpStatusCode = httpStatusCode;
    }

    public RetryableException(final String reason, final HttpStatusCode httpStatusCode,
                              final Throwable throwable) {
        super(throwable);
        this.reason = reason;
        this.httpStatusCode = httpStatusCode;
    }

    public RetryableException(final String reason, final HttpStatusCode httpStatusCode,
                              final Throwable throwable, final String message) {
        super(message, throwable);
        this.reason = reason;
        this.httpStatusCode = httpStatusCode;
    }

    public RetryableException(final String reason, final HttpStatusCode httpStatusCode,
                              final String message) {
        super(message);
        this.reason = reason;
        this.httpStatusCode = httpStatusCode;
    }

}
