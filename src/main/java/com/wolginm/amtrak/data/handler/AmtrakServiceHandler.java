package com.wolginm.amtrak.data.handler;

import com.wolginm.amtrak.data.models.gtfs.Agency;
import com.wolginm.amtrak.data.models.gtfs.Calendar;
import com.wolginm.amtrak.data.models.gtfs.Routes;
import com.wolginm.amtrak.data.properties.AmtrakProperties;
import com.wolginm.amtrak.data.util.CSVUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@DependsOn("AmtrakDataService")
public class AmtrakServiceHandler {

    private final List<Agency> agencies;
    private final List<Calendar> calendars;
    private final List<Routes> routes;

    private final AmtrakProperties amtrakProperties;
    private final CSVUtil csvUtil;

    //todo, see ipad data.
}
