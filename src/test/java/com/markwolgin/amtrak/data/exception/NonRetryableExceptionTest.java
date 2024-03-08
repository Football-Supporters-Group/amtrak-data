package com.markwolgin.amtrak.data.exception;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class NonRetryableExceptionTest {

    @Test
    void testNonRetryableException() {
        Assertions.assertThrows(NonRetryableException.class,
                () -> {
            throw new NonRetryableException();
                });
    }

}