package com.wolginm.amtrak.data;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.wolginm.amtrak.data.handler.AmtrakCalendarHandler;
import com.wolginm.amtrak.data.handler.AmtrakDataHandler;
import com.wolginm.amtrak.data.handler.AmtrakRoutesHandler;
import com.wolginm.amtrak.data.handler.AmtrakStopTimesHandler;
import com.wolginm.amtrak.data.properties.AmtrakProperties;
import com.wolginm.amtrak.data.service.AmtrakDataService;
import com.wolginm.amtrak.data.util.AmtrakRestTemplate;
import com.wolginm.amtrak.data.util.CSVUtil;
import com.wolginm.amtrak.data.util.FileUtil;
import com.wolginm.amtrak.data.util.ZipUtil;

import org.springframework.context.annotation.Import;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Import({AmtrakDataService.class, 
    AmtrakDataHandler.class, 
    AmtrakRestTemplate.class,
    AmtrakCalendarHandler.class,
    AmtrakRoutesHandler.class,
    AmtrakStopTimesHandler.class,
    CSVUtil.class,
    FileUtil.class,
    ZipUtil.class,
    AmtrakProperties.class})
public @interface EnableAmtrakData {
    
}
