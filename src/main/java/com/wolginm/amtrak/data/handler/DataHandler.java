package com.wolginm.amtrak.data.handler;

import java.io.IOException;

import com.wolginm.amtrak.data.properties.AmtrakProperties;
import com.wolginm.amtrak.data.util.AmtrakRestTemplate;
import com.wolginm.amtrak.data.util.ZipUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class DataHandler {
    
    private AmtrakProperties amtrakProperties;
    private AmtrakRestTemplate amtrakRestTemplate;
    private ZipUtil zipUtil;

    @Autowired
    public DataHandler(AmtrakProperties amtrakProperties,
        AmtrakRestTemplate amtrakRestTemplate,
        ZipUtil zipUtil) {
        this.amtrakProperties = amtrakProperties;
        this.amtrakRestTemplate = amtrakRestTemplate;
        this.zipUtil = zipUtil;
    }

    public int updateAmtrakDataFiles() {
        log.info("====== Update Amtrak Data Files ======");

        int status = 1;
        try {
            this.amtrakRestTemplate.downloadGTFSFile();
            this.zipUtil.unzip(this.amtrakProperties.getTempFile(), 
                this.amtrakProperties.getDataDirectory());

            status = 0;
        } catch (IOException e) {
            log.error("Failed to download Amtrak Data");
            e.printStackTrace();
        }

        log.info("====== End Amtrak Data Files ======");
        return status;
    }
}
