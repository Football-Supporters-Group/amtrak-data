package com.wolginm.amtrak.data;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class DataApplication {
	
	public static void main(String[] args) {
		ConfigurableApplicationContext context = SpringApplication.run(DataApplication.class, args);
		// AmtrakDataService amtrakDataService = context.getBean(AmtrakDataService.class);

		// amtrakDataService.prepDataForUse();
	}
}
