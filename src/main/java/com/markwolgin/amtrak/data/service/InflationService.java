package com.markwolgin.amtrak.data.service;

import com.markwolgin.amtrak.data.util.AmtrakFileNameToObjectUtil;
import com.markwolgin.amtrak.data.util.ObjectsUtil;
import com.markwolgin.amtrak.data.properties.AmtrakProperties;
import com.markwolgin.amtrak.data.models.AmtrakObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.file.NotDirectoryException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
public class InflationService {

    private final AmtrakFileNameToObjectUtil amtrakFileNameToObjectUtil;
    private final static String EMPTY_STRING = "";
    private final ObjectsUtil objectsUtil;
    private final String routeMetadata;
    private final Path tmpDir;
    private final Path tmpDataDir;

    /**
     * Used to inflate the flat objects.
     */
    public InflationService(final ObjectsUtil objectsUtil, final AmtrakFileNameToObjectUtil amtrakFileNameToObjectUtil,
                            final AmtrakProperties amtrakProperties,
                            @Qualifier("temporaryDirectoryPath") final Path tempDir) {
        log.info("AMTK-2100: Starting the Inflation Service");
        this.objectsUtil = objectsUtil;
        this.routeMetadata = amtrakProperties.getRoute_metadata();
        this.tmpDir = tempDir;
        this.tmpDataDir = Path.of(tmpDir.toString(), amtrakProperties.getGtfs().getDataDirectory());
        this.amtrakFileNameToObjectUtil = amtrakFileNameToObjectUtil;
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
    public <T extends AmtrakObject> List<T> inflateAmtrakObject(final Path inflatedObjectPath, final Class<T> tClass)
            throws FileNotFoundException {
        log.info("AMTK-2100: Attempting to parse object [{}] from path [{}]",
                tClass.getName(), inflatedObjectPath.toAbsolutePath());
        List<T> objectList = this.csvToObject(new FileInputStream(inflatedObjectPath.toFile()), tClass);
        if (objectList != null) log.info("AMTK-2110: Loaded in [{}] records of type [{}] from path [{}]",
                objectList.size(), tClass.getName(), inflatedObjectPath.toAbsolutePath());
        else log.error("AMTK-2199: Failed to load in records of type [{}] from path [{}]",
                tClass.getName(), inflatedObjectPath.toAbsolutePath());
        return objectList;
    }

    public <T extends AmtrakObject> Map<Class<T>, List<T>> inflateAllAmtrakObjects() throws NotDirectoryException {
        File directory = tmpDataDir.toFile();
        if (!directory.isDirectory()) {
            String error = String.format("AMTK-2199: Supplied directory [%s] was not a directory", tmpDataDir.toString());
            log.error(error);
            throw new NotDirectoryException(error);
        }
        Map<Class<T>, Path> amtrakObejctToPathMap = Arrays
                .stream(directory.listFiles())
                .parallel()
                .map(File::toPath)
                .collect(Collectors.toMap((path) -> this.amtrakFileNameToObjectUtil.getAmtrakObject(path),
                        (path) -> path));
        Map<Class<T>, List<T>> amtrakObjects = amtrakObejctToPathMap.entrySet().parallelStream().map(elem -> {
            try {
                return Map.entry(elem.getKey(), this.inflateAmtrakObject(elem.getValue(), elem.getKey()));
            } catch (FileNotFoundException e) {
                String err = "AMTK-2100: Got a bad file path from the OS [%s]".formatted(elem.getValue().toAbsolutePath());
                log.error(err);
                throw new RuntimeException(err, e);
            }
        }).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        return amtrakObjects;
    }

    public Map<String, LinkedHashSet<String>> inflateRouteOrderMetadata() throws FileNotFoundException {
        return this.csvToRouteOrderMap(new FileInputStream(Paths.get(this.routeMetadata).toFile()));
    }

    /**
     * Takes a csv file and converts it into a list of Objects.
     * @param inputStream   The contents of the CSV accessable as an input stream.
     * @param inputType     The type content the mapping will attempt.
     * @return              The list of objects.
     * @param <T>           The desired type to convert to.
     */
    public <T extends Serializable> List<T> csvToObject(final InputStream inputStream, final Class<T> inputType) {
        List<T> objects;
        List<String> headers;
        Map<String, String> objectElements;

        try {
            BufferedReader bufferedReader = new BufferedReader(
                    new InputStreamReader(inputStream, "UTF-8"));

            CSVParser csvParser = new CSVParser(bufferedReader,
                    CSVFormat.DEFAULT
                            .builder()
                            .setHeader()
                            .setSkipHeaderRecord(true)
                            .setIgnoreHeaderCase(true)
                            .setTrim(true)
                            .build());

            headers = csvParser.getHeaderNames();
            //Initializing the lists to be correct size off the jump for a perf increase.
            List<CSVRecord> cIterable = csvParser.getRecords();
            objectElements = headers
                    .stream()
                    .collect(Collectors.toMap(String::toString,
                        emptyString()));
            objects = new ArrayList<T>(cIterable.size());
            for (CSVRecord csvRecord : cIterable) {
                for (String header : headers) {
                    objectElements.put(header,csvRecord.get(header));
                }
                objects.add(objectsUtil.mapToObject(objectElements, inputType));
            }
            csvParser.close();
        } catch (IOException | IllegalArgumentException e) {
            log.error(e.getMessage());
            objects = null;
        }

        return objects;

    }

    public Map<String, LinkedHashSet<String>> csvToRouteOrderMap(final InputStream inputStream) {
        List<String> objects = null;
        Map<String, LinkedHashSet<String>> objectMap = null;

        try {
            BufferedReader bufferedReader = new BufferedReader(
                    new InputStreamReader(inputStream, "UTF-8"));

            CSVParser csvParser = new CSVParser(bufferedReader,
                    CSVFormat.DEFAULT
                            .builder()
                            .setHeader()
                            .setSkipHeaderRecord(true)
                            .setIgnoreHeaderCase(true)
                            .setTrim(true)
                            .build());

            objects = new ArrayList<String>();
            objectMap = new HashMap<>();
            Iterable<CSVRecord> cIterable = csvParser.getRecords();
            for (CSVRecord csvRecord : cIterable) {
                objects = csvRecord.toList();
                if (objects.size() > 1) {
                    objectMap.put(objects.get(0), new LinkedHashSet<>(objects.subList(1, objects.size())));
                } else {
                    log.error("CSV Parsing error for Route Map on row: {}, \"{}\"", csvRecord.getRecordNumber(), csvRecord.toString());
                }
            }
            csvParser.close();
        } catch (IOException e) {
            log.error(e.getMessage());
        }

        return objectMap;

    }

    private Function<String, String> emptyString() {
        return t->InflationService.EMPTY_STRING;
    }

}
