package com.wolginm.amtrak.data.models.gtfs;

import java.util.List;

import lombok.Data;

@Data
public class Routes implements ICVMapable {

    /**
     * The ID of the route.
     * Pk of Routes, Sk of {@link Trips}
     */
    private int route_id;
    /**
     * Operator of the route.
     * {@link Routes}
     */
    private int agency_id;
    private String route_short_name;
    private String route_long_name;
    private String route_type;
    private String route_url;
    private String route_color;
    private String route_text_color;

    @Override
    public ICVMapable mapToObject(List<Object> objectList, List<String> headersList) {
        Routes routes = new Routes();
        routes.setRoute_id(Integer.parseInt((String)objectList.get(0)));
        routes.setAgency_id(Integer.parseInt((String)objectList.get(1)));
        routes.setRoute_short_name((String) objectList.get(2));
        routes.setRoute_long_name((String)objectList.get(3));
        routes.setRoute_type((String)objectList.get(4));
        routes.setRoute_url((String)objectList.get(5));
        routes.setRoute_color((String)objectList.get(6));
        routes.setRoute_text_color((String)objectList.get(6));

        return routes;
    }

    @Override
    public boolean equals(Object o) {
        Routes other = (Routes) o;
        return this.getRoute_id() == other.getRoute_id();
    }
}
