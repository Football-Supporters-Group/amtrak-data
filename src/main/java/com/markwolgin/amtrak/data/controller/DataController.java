package com.markwolgin.amtrak.data.controller;

import com.markwolgin.amtrak.data.models.NonSuccessResponse;
import com.markwolgin.amtrak.data.service.DataManagementService;
import com.markwolgin.amtrak.data.models.ConsolidatedResponseObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Controller
@RequestMapping("/v1")
public class DataController {

    private DataManagementService dataManagementService;

    public DataController(final DataManagementService dataManagementService) {
        this.dataManagementService = dataManagementService;
    }

    @GetMapping("/routes")
    public ResponseEntity<ConsolidatedResponseObject> getRoutes() {
        log.info("AMTK-1010: In getRoutes - /routes");
        return ResponseEntity.status(200).body(this.dataManagementService.buildConsolidatedResponseObject());
    }

    @GetMapping("/route")
    public ResponseEntity<ConsolidatedResponseObject> getRoutes(@RequestParam(name = "ids") final String routeIds) {
        log.info("AMTK-1010: In getRoutes - /routes?id={}", routeIds);
        ResponseEntity<ConsolidatedResponseObject> responseObjectResponseEntity;
        if (routeIds != null && !routeIds.isEmpty() && !routeIds.isBlank()) {
            String[] elements = routeIds.split(",");

            try {
                Arrays.stream(elements).forEach(Integer::parseInt);
                responseObjectResponseEntity = ResponseEntity
                        .status(200)
                        .body(this.dataManagementService.buildConsolidatedResponseObject(routeIds));
            } catch (NumberFormatException exception) {
                String errorMsg = "Supplied ids are not valid integers [%s].  Check your deliminator (expected ,) and sent values".formatted(routeIds);
                log.error("AMTK-1019: {}", errorMsg, exception);
                throw exception;
            }
        } else {
            String errorMsg = "Null value supplied.";
            log.error("AMTK-1019: {}", errorMsg);
            throw new NullPointerException(errorMsg);
        }
        return responseObjectResponseEntity;
    }

    @ExceptionHandler(NumberFormatException.class)
    public ResponseEntity<NonSuccessResponse> handleException(NumberFormatException exception) {
        log.error("AMTK-1099: Number format exception triggered non 2xx response.", exception);
        return ResponseEntity.status(400)
                .body(new NonSuccessResponse(
                        List.of(exception.getMessage()),
                            400,
                            Instant.now().toString()));
    }

    @ExceptionHandler(NullPointerException.class)
    public ResponseEntity<NonSuccessResponse> handleException(NullPointerException exception) {
        log.error("AMTK-1099: Null pointer exception triggered non 2xx response.", exception);
        return ResponseEntity.status(400)
                .body(new NonSuccessResponse(
                        List.of(exception.getMessage()),
                        400,
                        Instant.now().toString()));
    }

}
