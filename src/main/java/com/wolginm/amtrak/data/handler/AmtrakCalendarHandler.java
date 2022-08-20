package com.wolginm.amtrak.data.handler;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.wolginm.amtrak.data.models.gtfs.Calendar;
import com.wolginm.amtrak.data.properties.AmtrakProperties;
import com.wolginm.amtrak.data.util.CSVUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class AmtrakCalendarHandler {
    
    
    private CSVUtil csvUtil;
    private AmtrakProperties amtrakProperties;

    private List<Calendar> calendars = new ArrayList<>();

    @Autowired
    public AmtrakCalendarHandler(CSVUtil csvUtil,
        AmtrakProperties amtrakProperties) {
        this.csvUtil = csvUtil;
        this.amtrakProperties = amtrakProperties;
    }

    public void loadRoutesFromFiles() throws FileNotFoundException {
        this.calendars = csvUtil.csvToObject(new FileInputStream(new File(
            String.format("%s/%s", this.amtrakProperties.getDataDirectory(), "calendar.txt"))), new Calendar())
            .stream().map((entity) -> {
                // log.info("{} / {} -> ", ((Calendar) entity).getService_id(), ((Calendar) entity).getStartDate().toString(), ((Calendar) entity).getEndDate().toString());
                return (Calendar) entity;
            }).collect(Collectors.toList());;
        log.info("Loaded {} calendar", this.calendars.size());
    }

    public Calendar findRouteByServiceId(String serviceID) {
        Calendar calendar = null;

        List<Calendar> acceptableService = this.calendars
            .stream()
            .filter((element) -> element.getService_id().equals(serviceID))
            .collect(Collectors.toList());
        if (acceptableService.size() != 1) {
            log.error("Unable to find specific calendar entry {}!", serviceID);
        } else {
            calendar = acceptableService.get(0);
            log.debug("Found calendar: {}", calendar);
        }

        return calendar;
    }

}
