package com.wolginm.amtrak.data.models.gtfs;

import java.util.List;
import java.util.Objects;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Deprecated
public class Shapes implements ICVMapable {
    
    private int shape_id;
    private double shape_pt_lat;
    private double shape_pt_lon;
    private int shape_pt_sequence;

    public Shapes() {}
 
    public Shapes(int shape_Id, double shape_pt_lat, 
            double shape_pt_lon, int shape_pt_sequence) {
        this.shape_id           = shape_Id;
        this.shape_pt_lat       = shape_pt_lat;
        this.shape_pt_lon       = shape_pt_lon;
        this.shape_pt_sequence  = shape_pt_sequence;
    }

    @Override
    public ICVMapable mapToObject(List<Object> objectList, List<String> headersList) {
        Shapes shapes = new Shapes();
        shapes.setShape_id(Integer.parseInt((String) objectList.get(0)));
        shapes.setShape_pt_lat(Double.parseDouble((String) objectList.get(1)));
        shapes.setShape_pt_lon(Double.parseDouble((String) objectList.get(2)));
        shapes.setShape_pt_sequence(Integer.parseInt((String) objectList.get(3)));
        return shapes;
    }

    @Override
    public boolean equals(Object obj) {
        boolean result;

        if (obj != null && obj instanceof Shapes) {
            Shapes other = (Shapes) obj;

            result   = Objects.equals(this.shape_id, other.shape_id)
                    && Objects.equals(this.shape_pt_lat, other.shape_pt_lat)
                    && Objects.equals(this.shape_pt_lon, other.shape_pt_lon)
                    && Objects.equals(this.shape_pt_sequence, other.shape_pt_sequence);
        } else result = false;

        return result;
    }
}
