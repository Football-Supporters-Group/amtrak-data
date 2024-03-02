package com.wolginm.amtrak.data;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.wolginm.amtrak.data.properties.AmtrakProperties;
import com.wolginm.amtrak.data.util.FileUtil;
import com.wolginm.amtrak.data.util.ZipUtil;

import org.springframework.context.annotation.Import;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Import({
    FileUtil.class,
    ZipUtil.class,
    AmtrakProperties.class})
public @interface EnableAmtrakData {
    
}
