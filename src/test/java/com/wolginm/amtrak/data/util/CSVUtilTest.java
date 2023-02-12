package com.wolginm.amtrak.data.util;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.assertj.core.util.Arrays;
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

    @Nested
    @DisplayName("CSV to Route Order Map")
    class CSVToRouteOrderMap {

        private String routeOrderList = "route_id,ordered_list\n" 
        + "40751,BOS,BBY,RTE,PVD,NHV,STM,NYP,NWK,MET,TRE,PHL,WIL,BAL,BWI,WAS\n" 
        + "95,NYP,YNY,CRT,POU,RHI,HUD,ALB,SDY,SAR,FED,WHL,FTC,POH,WSP,PRK,PLB,RSP,SLQ,MTR\n"
        + "94,HAR,MID,ELT,MJY,LNC,PAR,COT,DOW,EXT,PAO,ARD,PHL,CWH,TRE,PJC,NBK,MET,EWR,NWK,NYP\n"
        + "41042,SPG,WNL,WND,HFD,BER,MND,WFD,STS,NHV"; 

        @Test
        void csvToRouteOrderMap_Pass() {
            Map<Integer, List<String>> actual, expected;
            expected = new HashMap() {{
                put(40751, Arrays.asList("BOS,BBY,RTE,PVD,NHV,STM,NYP,NWK,MET,TRE,PHL,WIL,BAL,BWI,WAS".split(",")));
                put(95, Arrays.asList("NYP,YNY,CRT,POU,RHI,HUD,ALB,SDY,SAR,FED,WHL,FTC,POH,WSP,PRK,PLB,RSP,SLQ,MTR".split(",")));
                put(94, Arrays.asList("HAR,MID,ELT,MJY,LNC,PAR,COT,DOW,EXT,PAO,ARD,PHL,CWH,TRE,PJC,NBK,MET,EWR,NWK,NYP".split(",")));
                put(41042, Arrays.asList("SPG,WNL,WND,HFD,BER,MND,WFD,STS,NHV".split(",")));
            }};

            actual = csvUtil.csvToRouteOrderMap(new ByteArrayInputStream(this.routeOrderList.getBytes()));
            
            Assertions.assertArrayEquals(expected.get(40751).toArray(), actual.get(40751).toArray());
            Assertions.assertArrayEquals(expected.get(95).toArray(), actual.get(95).toArray());
            Assertions.assertArrayEquals(expected.get(94).toArray(), actual.get(94).toArray());
            Assertions.assertArrayEquals(expected.get(41042).toArray(), actual.get(41042).toArray());
        }

    }
    
}
