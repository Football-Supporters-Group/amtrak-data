package com.wolginm.amtrak.data.models.gtfs;

import java.util.List;

public interface ICVMapable {
    
    ICVMapable mapToObject(List<Object> objectList, List<String> headersList);
}
