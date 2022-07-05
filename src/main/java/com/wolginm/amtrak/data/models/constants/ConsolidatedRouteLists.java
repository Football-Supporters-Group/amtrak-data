package com.wolginm.amtrak.data.models.constants;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.wolginm.amtrak.data.properties.AmtrakProperties;
import com.wolginm.amtrak.data.util.CSVUtil;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class ConsolidatedRouteLists {

    private Map<Integer, List<String>> routeToOrderedList;

    @Autowired
    public ConsolidatedRouteLists(CSVUtil csvUtil,
        AmtrakProperties amtrakProperties) {
        try {
            this.routeToOrderedList = csvUtil.csvToRouteOrderMap(
                new FileInputStream(new File(
                    String.format("%s/%s", amtrakProperties.getRouteMetadata(), "route_stop_order.txt"))));
        } catch (FileNotFoundException e) {
            log.error("Failed to load route metadata! {}", e.getMessage());
            log.error("Defaulting routesToOrderedList to empty map!");
            this.routeToOrderedList = new HashMap<>();
            e.printStackTrace();
        }
    }


    public List<String> getStationList(int routeId) {
        List<String> selectedRoute = this.getStationList(routeId);

        if (selectedRoute == null) {
            log.error("Route {} returned null", routeId);
        } 

        return selectedRoute;
    }
}
