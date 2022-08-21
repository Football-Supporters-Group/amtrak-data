package com.wolginm.amtrak.data.service;

import java.util.List;

import com.wolginm.amtrak.data.handler.AmtrakDataHandler;
import com.wolginm.amtrak.data.models.consolidated.ConsolidatedRoute;
import com.wolginm.amtrak.data.models.consolidated.Trip;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
public class AmtrakDataService {
    
    private AmtrakDataHandler amtrakDataHandler;
    private boolean rePullFiles;

    @Autowired
    public AmtrakDataService(AmtrakDataHandler amtrakDataHandler, @Qualifier("NeedToPullData") boolean rePullFiles) {
        this.amtrakDataHandler = amtrakDataHandler;
        this.rePullFiles = rePullFiles;
        this.prepDataForUse();
    }

    public void prepDataForUse() {
        this.amtrakDataHandler.updateAmtrakDataFiles(this.rePullFiles);
        this.amtrakDataHandler.loadFilesToMemory();
        this.amtrakDataHandler.compileTrips();
        this.amtrakDataHandler.compileRoutes();
    }

    public List<Trip> getTrips() {
        return this.amtrakDataHandler.getTrips();
    }

    public List<ConsolidatedRoute> getRoutes() {
        return this.amtrakDataHandler.getRoutes();
    }
}
