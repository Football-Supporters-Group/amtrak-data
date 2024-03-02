package com.wolginm.amtrak.data.exception;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class NonRetryableExceptionTest {

    @Test
    void testNonRetryableException() {
        Assertions.assertThrows(NonRetryableException.class,
                () -> {
            throw new NonRetryableException();
                });
    }

}