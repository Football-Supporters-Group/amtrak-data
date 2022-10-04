package com.wolginm.amtrak.data.service;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

import com.wolginm.amtrak.data.handler.AmtrakDataHandler;
import com.wolginm.amtrak.data.models.consolidated.ConsolidatedRoute;
import com.wolginm.amtrak.data.models.consolidated.Trip;
import com.wolginm.amtrak.data.properties.AmtrakProperties;
import com.wolginm.amtrak.data.util.FileUtil;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class AmtrakDataService {
    
    private AmtrakDataHandler amtrakDataHandler;
    private AmtrakProperties amtrakProperties;
    private FileUtil fileUtil;

    @Autowired
    public AmtrakDataService(AmtrakDataHandler amtrakDataHandler, 
        AmtrakProperties amtrakProperties, FileUtil fileUtil) {
        this.amtrakDataHandler = amtrakDataHandler;
        this.amtrakProperties = amtrakProperties;
        this.fileUtil = fileUtil;
        this.prepDataForUse();
    }

    public void prepDataForUse() {
        boolean needToPullData = this.needToPullData();

        this.amtrakDataHandler.updateAmtrakDataFiles(needToPullData);
        this.amtrakDataHandler.loadFilesToMemory();
        this.amtrakDataHandler.compileTrips();
        this.amtrakDataHandler.compileRoutes();
    }

    public Boolean needToPullData() {
        boolean value = false;
        Path dataDirectory = this.fileUtil.resolvePath(this.amtrakProperties.getDataDirectory());
        log.info(this.amtrakProperties.getDataUpdateMs().toString());
        try {
            boolean directoryExists = fileUtil.directoryExists(dataDirectory);
            long age = fileUtil.getAgeInMili(dataDirectory);
            Integer update = Integer.valueOf(this.amtrakProperties.getDataUpdateMs());


            // value = directoryExists
            //     && fileUtil
            //         .compareAgeToConstantTime(age, update) < 0 ;
            value = true;
            log.debug("directoryExists: {}\nage: {}\nupdate delta: {}\nupdate value: {}", 
                directoryExists, age, update, value);
        } catch (NumberFormatException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } 
        return value;
    }

    public List<Trip> getTrips() {
        return this.amtrakDataHandler.getTrips();
    }

    public List<ConsolidatedRoute> getRoutes() {
        return this.amtrakDataHandler.getRoutes();
    }
}
