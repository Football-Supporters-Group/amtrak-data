package com.wolginm.amtrak.data.exception;

import org.junit.jupiter.api.Assertions;
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

        Assertions.assertEquals(code, retryableException.getHttpStatusCode());
        Assertions.assertEquals(reason, retryableException.getReason());
    }

    @Test
    void testReasonAndCode_Throwable() {
        this.retryableException = new RetryableException(reason, code, throwable);

        Assertions.assertEquals(code, retryableException.getHttpStatusCode());
        Assertions.assertEquals(reason, retryableException.getReason());
        Assertions.assertEquals(throwable, retryableException.getCause());
    }

    @Test
    void testReasonAndCode_Message_Throwable() {
        this.retryableException = new RetryableException(reason, code, message, throwable);

        Assertions.assertEquals(code, retryableException.getHttpStatusCode());
        Assertions.assertEquals(reason, retryableException.getReason());
        Assertions.assertEquals(message, retryableException.getMessage());
        Assertions.assertEquals(throwable, retryableException.getCause());
    }

    @Test
    void testReasonAndCode_Message() {
        this.retryableException = new RetryableException(reason, code, message);

        Assertions.assertEquals(code, retryableException.getHttpStatusCode());
        Assertions.assertEquals(reason, retryableException.getReason());
        Assertions.assertEquals(message, retryableException.getMessage());
    }


}