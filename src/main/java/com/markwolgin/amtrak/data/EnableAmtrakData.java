package com.markwolgin.amtrak.data;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.markwolgin.amtrak.data.util.FileUtil;
import com.markwolgin.amtrak.data.util.ZipUtil;
import com.markwolgin.amtrak.data.properties.AmtrakProperties;

import org.springframework.context.annotation.Import;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Import({
    FileUtil.class,
    ZipUtil.class,
    AmtrakProperties.class})
public @interface EnableAmtrakData {
    
}
