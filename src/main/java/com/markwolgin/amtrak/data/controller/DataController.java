package com.markwolgin.amtrak.data.controller;

import com.markwolgin.amtrak.data.service.DataManagementService;
import com.markwolgin.amtrak.data.models.ConsolidatedResponseObject;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

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
