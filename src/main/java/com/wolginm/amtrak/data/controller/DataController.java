package com.wolginm.amtrak.data.controller;

import com.wolginm.amtrak.data.service.DataManagementService;
import com.wolginm.amtrak.data.service.InflationService;
import com.wolginmark.amtrak.data.models.ConsolidatedObjects;
import com.wolginmark.amtrak.data.models.ConsolidatedResponseObject;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.xml.crypto.Data;

@Controller
@RequestMapping("/v1")
public class DataController {

    private DataManagementService dataManagementService;

    public DataController(final DataManagementService dataManagementService) {
        this.dataManagementService = dataManagementService;
    }

    @GetMapping("/routes")
    public ResponseEntity<ConsolidatedResponseObject> getRoutes() {
        return ResponseEntity.status(200).body(this.dataManagementService.buildConsolidatedResponseObject());
    }

}
