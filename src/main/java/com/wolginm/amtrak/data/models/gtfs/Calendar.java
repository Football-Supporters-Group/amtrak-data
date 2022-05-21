package com.wolginm.amtrak.data.models.gtfs;

import java.sql.Date;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Calendar implements ICVMapable{
    
    private String service_id;
    private boolean monday;
    private boolean tuesday;
    private boolean wednesday;
    private boolean thursday;
    private boolean friday;
    private boolean saturday;
    private boolean sunday;
    private Date startDate;
    private Date endDate;


    @Override
    public ICVMapable mapToObject(List<Object> objectList, List<String> headerList) {
        Calendar calendar = new Calendar();
        calendar.setService_id((String) objectList.get(0));
        calendar.setMonday(this.parseBoolean(objectList.get(1)));
        calendar.setTuesday(this.parseBoolean(objectList.get(2)));
        calendar.setWednesday(this.parseBoolean(objectList.get(3)));
        calendar.setThursday(this.parseBoolean(objectList.get(4)));
        calendar.setFriday(this.parseBoolean(objectList.get(5)));
        calendar.setSaturday(this.parseBoolean(objectList.get(6)));
        calendar.setSunday(this.parseBoolean(objectList.get(7)));
        calendar.setStartDate(this.parseDate(objectList.get(8)));
        calendar.setEndDate(this.parseDate(objectList.get(8)));
        return calendar;
    }

    private boolean parseBoolean(Object bool) {
        return ((String) bool).equals("1") ? true : false;
    }

    /**
     * Parses yyyymmdd to yyyy-mm-dd for SQL
     */
    private Date parseDate(Object date) {
        String modifiedDate = String.format("%s-%s-%s", 
            ((String) date).substring(0, 4), 
            ((String) date).substring(4, 6), 
            ((String) date).substring(6));
        return Date.valueOf(modifiedDate);
    }
}
