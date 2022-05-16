package com.wolginm.amtrak.data.models.gtfs;

import java.sql.Date;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FeedInfo {
    
    private String feed_publisher_name;
    private String feed_publisher_url;
    private String feed_lang;
    private String default_lang;
    private Date feed_start_date;
    private Date feed_end_date;
    private String feed_version;
    private String feed_contact_email;
    private String feed_contact_url;
}
