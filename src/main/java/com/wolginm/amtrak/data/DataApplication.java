package com.wolginm.amtrak.data;

import com.wolginm.amtrak.data.handler.DataHandler;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class DataApplication {
	
	public static void main(String[] args) {
		ConfigurableApplicationContext context = SpringApplication.run(DataApplication.class, args);

		DataHandler dataHandler = context.getBean(DataHandler.class);
		dataHandler.updateAmtrakDataFiles();
	}



}
