package com.markwolgin.amtrak.data.service;

import io.swagger.v3.oas.models.security.SecurityScheme;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.SchedulingException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.Instant;

/**
 * To periodically refresh the data in the system, to prevent it from getting stale.
 */
@Slf4j
@Service
public class ScheduleService {

    /**
     * Data Management Service to pull data from.
     */
    private final DataManagementService dataManagementService;

    @Autowired
    public ScheduleService(final DataManagementService dataManagementService) {
        this.dataManagementService = dataManagementService;
        log.info("AMTK-2300: Started the ScheduleService");
    }

    /**
     * Will trigger the data refresh sub-routine.  This wil re-acquire Amtrak Data Daily,
     *  and load it overnight to prepare for more calls later.
     * @return  The instant the refresh finishes.
     */
    @Scheduled(cron = "0 2 * * * *")
    public Instant triggerDataRefresh() {
        log.info("AMTK-2310: Data Refresh Triggered at [{}].  Time of last refresh [{}]", Instant.now(), this.dataManagementService.getLastTimeLastRefresh());

        try {
            this.dataManagementService.loadAmtrakDataIntoLocal();
        } catch (RuntimeException e) {
            log.error("AMTK-2319: Data Refresh has failed due to exception [{}]", e.getMessage(), e);
            throw new SchedulingException("AMTK-2319: Data Refresh has failed due to exception %s".formatted(e.getMessage()), e);
        }

        log.info("AMTK-2311: Data successfully refreshed");
        return Instant.now();
    }

}
