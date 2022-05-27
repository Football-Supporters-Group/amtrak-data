package com.wolginm.amtrak.data;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.wolginm.amtrak.data.properties.AmtrakProperties;
import com.wolginm.amtrak.data.service.AmtrakDataService;

import org.springframework.context.annotation.Import;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Import({AmtrakDataService.class, AmtrakProperties.class})
public @interface EnableAmtrakData {
    
}
