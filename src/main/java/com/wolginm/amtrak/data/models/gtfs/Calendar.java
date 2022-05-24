package com.wolginm.amtrak.data.models.gtfs;

import java.sql.Date;
import java.util.List;

import com.wolginm.amtrak.data.util.GTFSUtil;

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
        calendar.setMonday(GTFSUtil.parseBoolean(objectList.get(1)));
        calendar.setTuesday(GTFSUtil.parseBoolean(objectList.get(2)));
        calendar.setWednesday(GTFSUtil.parseBoolean(objectList.get(3)));
        calendar.setThursday(GTFSUtil.parseBoolean(objectList.get(4)));
        calendar.setFriday(GTFSUtil.parseBoolean(objectList.get(5)));
        calendar.setSaturday(GTFSUtil.parseBoolean(objectList.get(6)));
        calendar.setSunday(GTFSUtil.parseBoolean(objectList.get(7)));
        calendar.setStartDate(GTFSUtil.parseDate(objectList.get(8)));
        calendar.setEndDate(GTFSUtil.parseDate(objectList.get(8)));
        return calendar;
    }

    @Override
    public boolean equals(Object o) {
        Calendar other = (Calendar) o;
        return this.getService_id().equals(other.getService_id());
    }

}
