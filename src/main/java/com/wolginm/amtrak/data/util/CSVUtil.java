package com.wolginm.amtrak.data.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

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
}
