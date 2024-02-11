package com.wolginm.amtrak.data.util;

import com.wolginmark.amtrak.data.models.AmtrakObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.nio.file.Path;

@Slf4j
@Component
public class AmtrakFileNameToObjectUtil {

    public AmtrakObject getAmtrakObject(Path path) {
        return this.getAmtrakObject(path.getFileName().getName())
    }

    public AmtrakObject getAmtrakObject(String name) {

    }

}
