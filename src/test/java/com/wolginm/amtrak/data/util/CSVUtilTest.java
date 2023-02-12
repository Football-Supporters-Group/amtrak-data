package com.wolginm.amtrak.data.util;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.util.Chars;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import com.wolginm.amtrak.data.models.gtfs.ICVMapable;
import com.wolginm.amtrak.data.models.gtfs.Shapes;

@ExtendWith(MockitoExtension.class)
public class CSVUtilTest {

    @InjectMocks
    private CSVUtil csvUtil;

    @Nested
    @DisplayName("CSV to Object Tests")
    class csvToObject {

        /*
         *  private int shape_id;
            private double shape_pt_lat;
            private double shape_pt_lon;
            private int shape_pt_sequence;
         */
        private String sampleShapeCSV = "shape_id, shape_pt_lat, shape_pt_long, shape_pt_sequence\n" 
            + "1, 1.0,  -1.0,  1\n"
            + "2, 3.14, -6.28, 2"; 

        @Test
        void csvToObject_Pass() {
            List<ICVMapable> actual, expected;
            expected = new ArrayList<>() {{
                add(new Shapes(1, 1.0, -1.0, 1));
                add(new Shapes(2, 3.14, -6.28, 2));
            }};

            actual = csvUtil.csvToObject(
                new ByteArrayInputStream(
                    sampleShapeCSV.getBytes()), new Shapes());
            
            Assertions.assertEquals(2, actual.size());
            Assertions.assertArrayEquals(expected.toArray(), actual.toArray());
        }

        @Test
        void csvToObject_Fail_NumberFormatException() {

            Assertions.assertEquals(new ArrayList<>(), csvUtil.csvToObject(
                new ByteArrayInputStream("SUpper: Broken---%2039not.\n csv:\n BRokwnd".getBytes()), new Shapes()));
        }
    }
    
}
