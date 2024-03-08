package com.markwolgin.amtrak.data.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.scheduling.SchedulingException;

import java.io.IOException;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ScheduleServiceTest {

    @Mock
    private DataManagementService dataManagementService;

    @InjectMocks
    private ScheduleService scheduleService;

    @BeforeEach
    void setUp() {
        Mockito.when(dataManagementService.getLastTimeLastRefresh()).thenReturn(Instant.MIN);
    }

    @Test
    void triggerDataRefresh_Pass() throws IOException {
        Mockito.when(dataManagementService.loadAmtrakDataIntoLocal()).thenReturn(null);

        Instant actual = scheduleService.triggerDataRefresh();
        Assertions.assertTrue(actual.isBefore(Instant.now()));
    }

    @Test
    void triggerDataRefresh_ThrowsException() throws IOException {
        Mockito.when(dataManagementService.loadAmtrakDataIntoLocal()).thenThrow(new IOException());

        Assertions.assertThrows(SchedulingException.class,
                () -> scheduleService.triggerDataRefresh());
    }
}