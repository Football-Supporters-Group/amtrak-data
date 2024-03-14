package com.markwolgin.amtrak.data.controller;

import com.markwolgin.amtrak.data.models.ConsolidatedResponseObject;
import com.markwolgin.amtrak.data.models.NonSuccessResponse;
import com.markwolgin.amtrak.data.service.DataManagementService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.openapitools.jackson.nullable.JsonNullable;
import org.springframework.http.ResponseEntity;

import java.time.Instant;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.refEq;

@ExtendWith(MockitoExtension.class)
class DataControllerTest {

    @Mock
    private DataManagementService dataManagementService;

    @InjectMocks
    private DataController dataController;

    private final Instant now = Instant.now();

    @Nested
    @DisplayName("Get route by Id")
    class GetRoute {

        @Test
        void testGetWithNoIdsEmptyString() {
            Assertions.assertThrows(NullPointerException.class, () -> dataController.getRoutes(""));
        }

        @Test
        void testGetWithNoIdsBlankString() {
            Assertions.assertThrows(NullPointerException.class, () -> dataController.getRoutes("         "));
        }

        @Test
        void testGetWithNoIdsNull() {
            Assertions.assertThrows(NullPointerException.class, () -> dataController.getRoutes(null));
        }

        @Test
        void testGetWithOneId() {
            String[] routeIds = {"1"};
            ConsolidatedResponseObject expected = new ConsolidatedResponseObject()
                    .requestedConsolidatedRoutes(new HashMap<>())
                            .lastTimeDataWasRefreshed(now.toString())
                                    .requestedRouteIds(Arrays.stream(routeIds).toList());

            Mockito.when(dataManagementService.buildConsolidatedResponseObject(eq(routeIds))).thenReturn(expected);

            ResponseEntity<ConsolidatedResponseObject> actual = dataController.getRoutes("1");

            Assertions.assertEquals(expected, actual.getBody());
        }

        @Test
        void testGetWithManyId() {
            String[] routeIds = {"1","2"};
            ConsolidatedResponseObject expected = new ConsolidatedResponseObject()
                    .requestedConsolidatedRoutes(new HashMap<>())
                    .lastTimeDataWasRefreshed(now.toString())
                    .requestedRouteIds(Arrays.stream(routeIds).toList());

            Mockito.when(dataManagementService.buildConsolidatedResponseObject(eq("1,2"))).thenReturn(expected);

            ResponseEntity<ConsolidatedResponseObject> actual = dataController.getRoutes("1,2");

            Assertions.assertEquals(expected, actual.getBody());
        }

        @Test
        void testGetWithNotAnInt() {
            Assertions.assertThrows(NumberFormatException.class, () -> dataController.getRoutes("a"));
        }

        @Test
        void testGetWithBadDeliminator() {
            Assertions.assertThrows(NumberFormatException.class, () -> dataController.getRoutes("1;2"));
        }

    }

    @Nested
    @DisplayName("Get all routes")
    class GetRoutes {
        @Test
        void testGetAllRoutes() {
            ConsolidatedResponseObject expected = new ConsolidatedResponseObject()
                    .requestedConsolidatedRoutes(new HashMap<>())
                    .lastTimeDataWasRefreshed(now.toString())
                    .requestedRouteIds(List.of("1", "2", "3"));

            Mockito.when(dataManagementService.buildConsolidatedResponseObject()).thenReturn(expected);

            ResponseEntity<ConsolidatedResponseObject> actual = dataController.getRoutes();

            Assertions.assertEquals(expected, actual.getBody());
        }
    }

    @Nested
    @DisplayName("Testing Different Exceptions")
    class ExceptionHandlingTests {

        @Test
        void numberFormattingException() {
            List<String> additionalContect = List.of("Additional Context");
            NumberFormatException numberFormatException = new NumberFormatException(additionalContect.get(0));

            NonSuccessResponse actual, expected;
            expected = new NonSuccessResponse().statusCode(400).additionalContext(additionalContect);
            ResponseEntity<NonSuccessResponse> responseEntity = dataController.handleException(numberFormatException);
            actual = responseEntity.getBody();

            Assertions.assertEquals(expected.getStatusCode(), actual.getStatusCode());
            Assertions.assertEquals(expected.getAdditionalContext(), actual.getAdditionalContext());
            Assertions.assertInstanceOf(Instant.class, Instant.parse(actual.getTimestamp()));
            Assertions.assertEquals(expected.getStatusCode(), responseEntity.getStatusCode().value());
        }

        @Test
        void nullPointerException() {
            List<String> additionalContect = List.of("Additional Context");
            NullPointerException nullPointerException = new NullPointerException(additionalContect.get(0));

            NonSuccessResponse actual, expected;
            expected = new NonSuccessResponse().statusCode(400).additionalContext(additionalContect);
            ResponseEntity<NonSuccessResponse> responseEntity = dataController.handleException(nullPointerException);
            actual = responseEntity.getBody();

            Assertions.assertEquals(expected.getStatusCode(), actual.getStatusCode());
            Assertions.assertEquals(expected.getAdditionalContext(), actual.getAdditionalContext());
            Assertions.assertInstanceOf(Instant.class, Instant.parse(actual.getTimestamp()));
            Assertions.assertEquals(expected.getStatusCode(), responseEntity.getStatusCode().value());
        }
    }
}