package com.wolginm.amtrak.data.service;

import com.wolginm.amtrak.data.properties.AmtrakProperties;
import com.wolginm.amtrak.data.util.CSVUtil;
import com.wolginmark.amtrak.data.models.AmtrakObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class InflationService {

    private final CSVUtil csvUtil;

    /**
     * Used to inflate the flat objects.
     * @param csvUtil           CSV Utility to handle the recursive calls.
     */
    public InflationService(final CSVUtil csvUtil) {
        this.csvUtil = csvUtil;

        log.info("AMTK-2100: Starting the Inflation Service");
    }

    /**
     * Makes a call to the CSV Util to load the specific file (routes, agency, etc...)
     *  into a {@link List<AmtrakObject>} of type tClass, which extends {@link AmtrakObject}.
     * @param inflatedObjectPath    The path of the object to inflate.
     * @param tClass                The type of the object to cast inflation to.
     * @return                      The list of the inflated objects.
     * @param <T>                   The specific implementaion of the object.
     * @throws FileNotFoundException    Could not find the object.
     */
    public <T extends AmtrakObject> List<T> inflateAmtrakObject(Path inflatedObjectPath, Class<T> tClass)
            throws FileNotFoundException {
        log.info("AMTK-2100: Attempting to parse object [{}] from path [{}]",
                tClass.getName(), inflatedObjectPath.toAbsolutePath());

        return new ArrayList<>(csvUtil.csvToObject(new FileInputStream(inflatedObjectPath.toFile()), tClass));
    }

}
