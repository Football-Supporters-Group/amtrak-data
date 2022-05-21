package com.wolginm.amtrak.data;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import com.wolginm.amtrak.data.handler.DataHandler;
import com.wolginm.amtrak.data.models.gtfs.Calendar;
import com.wolginm.amtrak.data.models.gtfs.ICVMapable;
import com.wolginm.amtrak.data.util.CSVUtil;
import com.wolginm.amtrak.data.util.ZipUtil;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class DataApplication {
	
	public static void main(String[] args) {
		ConfigurableApplicationContext context = SpringApplication.run(DataApplication.class, args);

		DataHandler dataHandler = context.getBean(DataHandler.class);
		ZipUtil zipUtil = context.getBean(ZipUtil.class);
		dataHandler.updateAmtrakDataFiles();
		try {
			zipUtil.unzip("/home/wolginm/git/amtrak/amtrak-data/data/tmp/gtfs.zip", "/home/wolginm/git/amtrak/amtrak-data/data/");
		} catch (IOException e) {
			e.printStackTrace();
		}

		try {
			InputStream reader = new FileInputStream(new File("/home/wolginm/git/amtrak/amtrak-data/data/calendar.txt"));


			CSVUtil csvUtil = context.getBean(CSVUtil.class);
			List<ICVMapable> mapped = csvUtil.csvToObject(reader, new Calendar());
			System.out.print(mapped.size());
		} catch (Exception e) {
			e.printStackTrace();
		}
		

	}
}
