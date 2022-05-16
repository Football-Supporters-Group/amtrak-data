package com.wolginm.amtrak.data.models.gtfs;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Agency {
    
    private int agency_id;
    private String agency_name;
    private String agency_url;
    private String agency_timezone;
    private String agency_lang;
}
