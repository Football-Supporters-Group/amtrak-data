package com.wolginm.amtrak.data.util;

import java.sql.Date;
import java.sql.Time;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class GTFSUtil {

    public static boolean parseBoolean(Object bool) {
        return ((String) bool).equals("1") ? true : false;
    }

    /**
     * Parses yyyymmdd to yyyy-mm-dd for SQL
     */
    public static Date parseDate(Object date) {
        Date parsedDate = null;
        if ( date != null && ((String) date).length() ==8 ) {
            String modifiedDate = String.format("%s-%s-%s", 
            ((String) date).substring(0, 4), 
            ((String) date).substring(4, 6), 
            ((String) date).substring(6));
            parsedDate = Date.valueOf(modifiedDate);
        }
        
        return parsedDate;
    }

    public static LocalTime parseTime(Object time) {
        String[] splitTime = ((String) time).split(":");
        int hour = Integer.parseInt(splitTime[0]);
        
        String parsedString = String.format("%02d:%02d:%02d", 
            hour < 24 ? hour : 0, 
            Integer.parseInt(splitTime[1]), 
            Integer.parseInt(splitTime[2]));

        LocalTime parsedTime = LocalTime
            .parse(parsedString, 
                DateTimeFormatter.ofPattern("HH:mm:ss"));
        return parsedTime;
    }
}
