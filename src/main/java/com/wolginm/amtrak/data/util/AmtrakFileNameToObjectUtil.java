package com.wolginm.amtrak.data.util;

import com.wolginmark.amtrak.data.models.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.message.StringFormattedMessage;
import org.springframework.stereotype.Component;

import java.nio.file.Path;

@Slf4j
@Component
public class AmtrakFileNameToObjectUtil {

    public <T extends AmtrakObject> Class<T> getAmtrakObject(Path path) {
        return this.getAmtrakObject(path.toFile().getName());
    }

    public Class getAmtrakObject(String name) {
        if (name.contains(".txt")) name = name.replace(".txt", "");

        return switch(name) {
            case "agency" -> Agency.class;
            case "calendar" -> Calendar.class;
            case "feed_info" -> FeedInfo.class;
            case "routes" -> Routes.class;
            case "shapes" -> Shapes.class;
            case "stops" -> Stops.class;
            case "stop_times" -> StopTimes.class;
            case "transfers" -> Transfers.class;
            case "trips" -> Trips.class;
            default ->  {
                String err = String.format("AMTK-6599: Supplied Class Key was not a valid class key [%s]", name);
                log.error(err);
                throw new NoSuchFieldError(err);
            }
        };

    }

}
