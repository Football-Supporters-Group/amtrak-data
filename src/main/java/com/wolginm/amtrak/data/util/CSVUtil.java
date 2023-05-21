package com.wolginm.amtrak.data.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.wolginm.amtrak.data.models.gtfs.ICVMapable;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class CSVUtil {
    
    public List<ICVMapable> csvToObject(InputStream inputStream, ICVMapable inputType) {
        List<ICVMapable> objects = null;
        List<String> headers = null;
        List<Object> objectList = null;

        try {
            BufferedReader bufferedReader = new BufferedReader(
                new InputStreamReader(inputStream, "UTF-8"));
            ;
            CSVParser csvParser = new CSVParser(bufferedReader, 
                CSVFormat.DEFAULT
                    .builder()
                    .setHeader()
                    .setSkipHeaderRecord(true)
                    .setIgnoreHeaderCase(true)
                    .setTrim(true)
                    .build());
            
            objects = new ArrayList<ICVMapable>();
            headers = csvParser.getHeaderNames();
            objectList = new ArrayList<>();
            Iterable<CSVRecord> cIterable = csvParser.getRecords();
            for (CSVRecord csvRecord : cIterable) {
                for (String header : headers) {
                    objectList.add(csvRecord.get(header));
                }
                objects.add(inputType.mapToObject(objectList, headers));
                objectList.clear();
            }
            csvParser.close();
        } catch (UnsupportedEncodingException e) {
            log.error(e.getMessage());
        } catch (IOException e) {
            log.error(e.getMessage());
        }
        
        return objects;
        
    }

    public Map<Integer, List<String>> csvToRouteOrderMap(InputStream inputStream) {
        List<String> objects = null;
        Map<Integer, List<String>> objectMap = null;

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
                    objectMap.put(Integer.parseInt(objects.get(0)), objects.subList(1, objects.size()));
                } else {
                    log.error("CSV Parsing error for Route Map on row: {}, \"{}\"", csvRecord.getRecordNumber(), csvRecord.toString());
                }
            }
            csvParser.close();
        } catch (UnsupportedEncodingException e) {
            log.error(e.getMessage());
        } catch (IOException e) {
            log.error(e.getMessage());
        }
        
        return objectMap;
        
    }
}
