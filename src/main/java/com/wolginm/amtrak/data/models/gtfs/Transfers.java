package com.wolginm.amtrak.data.models.gtfs;

import java.util.List;

import lombok.Data;


/**
 * Not currently used.
 */
@Data
public class Transfers implements ICVMapable {
    
    private String from_stop_id;
    private String to_stop_id;
    private int transfer_type;

    /**
     * In mins
     */
    private int min_transfer_time;

    @Override
    public ICVMapable mapToObject(List<Object> objectList, List<String> headersList) {
        Transfers transfers = new Transfers();
        int transferTime = ((String) objectList.get(3)).length() > 0 ? Integer.parseInt((String) objectList.get(3)) : 0;
        transfers.setFrom_stop_id((String) objectList.get(0));
        transfers.setTo_stop_id((String) objectList.get(1));
        transfers.setTransfer_type(Integer.parseInt((String) objectList.get(2)));
        transfers.setMin_transfer_time(transferTime);
        return transfers;
    }
}
