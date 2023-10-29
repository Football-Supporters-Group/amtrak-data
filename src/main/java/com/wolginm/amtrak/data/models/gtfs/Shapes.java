package com.wolginm.amtrak.data.models.gtfs;

import java.util.List;

import lombok.Data;

/**
 * Not used.`
 */
@Data
@Deprecated
public class Shapes implements ICVMapable {
    
    private int shape_id;
    private double shape_pt_lat;
    private double shape_pt_lon;
    private int shape_pt_sequence;


    @Override
    public ICVMapable mapToObject(List<Object> objectList, List<String> headersList) {
        Shapes shapes = new Shapes();
        shapes.setShape_id(Integer.parseInt((String) objectList.get(0)));
        shapes.setShape_pt_lat(Double.parseDouble((String) objectList.get(1)));
        shapes.setShape_pt_lon(Double.parseDouble((String) objectList.get(2)));
        shapes.setShape_pt_sequence(Integer.parseInt((String) objectList.get(3)));
        return shapes;
    }
}
