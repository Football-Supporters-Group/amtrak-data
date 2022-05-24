package com.wolginm.amtrak.data.handler;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.wolginm.amtrak.data.models.gtfs.Routes;
import com.wolginm.amtrak.data.properties.AmtrakProperties;
import com.wolginm.amtrak.data.util.CSVUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class AmtrakRoutesHandler {
    
    
    private CSVUtil csvUtil;
    private AmtrakProperties amtrakProperties;

    private List<Routes> routes = new ArrayList<>();

    @Autowired
    public AmtrakRoutesHandler(CSVUtil csvUtil,
        AmtrakProperties amtrakProperties) {
        this.csvUtil = csvUtil;
        this.amtrakProperties = amtrakProperties;
    }

    public void loadRoutesFromFiles() throws FileNotFoundException {
        this.routes = csvUtil.csvToObject(new FileInputStream(new File(
            String.format("%s/%s", this.amtrakProperties.getDataDirectory(), "routes.txt"))), new Routes())
            .stream().map((entity) -> {
                return (Routes) entity;
            }).collect(Collectors.toList());;
        log.info("Loaded {} routes", this.routes.size());
    }

    public Routes findRouteByRoutesId(int routeID) {
        Routes route = null;

        List<Routes> acceptableRoutes = this.routes
            .stream()
            .filter((element) -> element.getRoute_id() == routeID)
            .collect(Collectors.toList());
        if (acceptableRoutes.size() != 1) {
            log.error("Unable to find specific route {}!", routeID);
        } else {
            route = acceptableRoutes.get(0);
        }

        return route;
    }

    public List<Routes> getAllRoutes() {
        return this.routes;
    }

}
