package com.wolginm.amtrak.data;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
@EnableConfigurationProperties
public class DataApplication {
	
	public static void main(String[] args) {
		ConfigurableApplicationContext context = SpringApplication.run(DataApplication.class, args);
	}
}
