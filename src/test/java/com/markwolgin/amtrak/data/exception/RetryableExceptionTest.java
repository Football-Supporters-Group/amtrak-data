package com.markwolgin.amtrak.data.exception;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatusCode;

import static org.junit.jupiter.api.Assertions.*;

class RetryableExceptionTest {

    private final HttpStatusCode code = HttpStatusCode.valueOf(202);
    private final String reason = "Accepted";
    private final String message = "Hello All.";
    private final Throwable throwable = new IllegalArgumentException();

    private RetryableException retryableException;

    @Test
    void testReasonAndCode() {
        this.retryableException = new RetryableException(reason, code);

        assertEquals(code, retryableException.getHttpStatusCode());
        assertEquals(reason, retryableException.getReason());
    }

    @Test
    void testReasonAndCode_Throwable() {
        this.retryableException = new RetryableException(reason, code, throwable);

        assertEquals(code, retryableException.getHttpStatusCode());
        assertEquals(reason, retryableException.getReason());
        assertEquals(throwable, retryableException.getCause());
    }

    @Test
    void testReasonAndCode_Message_Throwable() {
        this.retryableException = new RetryableException(reason, code, message, throwable);

        assertEquals(code, retryableException.getHttpStatusCode());
        assertEquals(reason, retryableException.getReason());
        assertEquals(message, retryableException.getMessage());
        assertEquals(throwable, retryableException.getCause());
    }

    @Test
    void testReasonAndCode_Message() {
        this.retryableException = new RetryableException(reason, code, message);

        assertEquals(code, retryableException.getHttpStatusCode());
        assertEquals(reason, retryableException.getReason());
        assertEquals(message, retryableException.getMessage());
    }


}