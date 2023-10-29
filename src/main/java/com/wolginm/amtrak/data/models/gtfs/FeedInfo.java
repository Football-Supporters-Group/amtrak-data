package com.wolginm.amtrak.data.models.gtfs;

import java.sql.Date;
import java.util.List;

import com.wolginm.amtrak.data.util.GTFSUtil;

import lombok.Data;

/**
 * Not used.
 */
@Data
public class FeedInfo implements ICVMapable {
    
    private String feed_publisher_name;
    private String feed_publisher_url;
    private String feed_lang;
    private String default_lang;
    private Date feed_start_date;
    private Date feed_end_date;
    private String feed_version;
    private String feed_contact_email;
    private String feed_contact_url;


    @Override
    public ICVMapable mapToObject(List<Object> objectList, List<String> headersList) {
        FeedInfo feedInfo = new FeedInfo();
        feedInfo.setFeed_publisher_name((String) objectList.get(0));
        feedInfo.setFeed_publisher_url((String) objectList.get(1));
        feedInfo.setFeed_lang((String) objectList.get(2));
        feedInfo.setDefault_lang((String) objectList.get(3));
        feedInfo.setFeed_start_date(GTFSUtil.parseDate(objectList.get(4)));
        feedInfo.setFeed_end_date(GTFSUtil.parseDate(objectList.get(5)));
        feedInfo.setFeed_version((String) objectList.get(6));
        feedInfo.setFeed_contact_email((String) objectList.get(7));
        feedInfo.setFeed_contact_url((String) objectList.get(8));
        return feedInfo;
    }
}
