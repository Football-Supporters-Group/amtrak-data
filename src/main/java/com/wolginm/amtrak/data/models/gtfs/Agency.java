package com.wolginm.amtrak.data.models.gtfs;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Agency implements ICVMapable {
    
    private int agency_id;
    private String agency_name;
    private String agency_url;
    private String agency_timezone;
    private String agency_lang;

    @Override
    public ICVMapable mapToObject(List<Object> objectList, List<String> headerString) {
        Agency agency = new Agency();
        agency.setAgency_id(Integer.parseInt((String) objectList.get(0)));
        agency.setAgency_name((String) objectList.get(1));
        agency.setAgency_url((String) objectList.get(2));
        agency.setAgency_timezone((String) objectList.get(3));
        agency.setAgency_lang((String) objectList.get(4));

        return agency;
    }
}
