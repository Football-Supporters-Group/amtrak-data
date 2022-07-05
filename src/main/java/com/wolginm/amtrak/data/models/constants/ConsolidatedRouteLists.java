package com.wolginm.amtrak.data.models.constants;

import java.util.Arrays;
import java.util.List;

public class ConsolidatedRouteLists {

    private final List<String> adirondackTrailways = Arrays.asList(new String[]{"NYP", "YNY", "CRT", "POU",
        "RHI", "HUD", "ALB", "SDY", "SAR", "FED", "WHL", "FTC", "POH", "WSP", "PRK", "PLB", "RSP", 
        "SLQ", "MTR"});

    private final List<String> keystoneService = Arrays.asList(new String[]{"HAR", "MID", "ELT", "MJY", 
        "LNC", "PAR", "COT", "DOW", "EXT", "PAO", "ARD", "PHL", "CWH", "TRE", "PJC", "NBK", "MET", "EWR",
        "NWK", "NYP"});
    
    public List<String> getStationList(int routeId) {
        List<String> selectedRoute;

        switch(routeId) {
            case 94:
                selectedRoute = this.keystoneService;
                break;
            case 95:
                selectedRoute = this.adirondackTrailways;
                break;
            default:
                selectedRoute = null;
                break;
        }

        return selectedRoute;
    }
}
