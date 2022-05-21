package com.wolginm.amtrak.data;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import com.wolginm.amtrak.data.handler.DataHandler;
import com.wolginm.amtrak.data.models.gtfs.*;
import com.wolginm.amtrak.data.util.CSVUtil;
import com.wolginm.amtrak.data.util.ZipUtil;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class DataApplication {
	
	public static void main(String[] args) {
		ConfigurableApplicationContext context = SpringApplication.run(DataApplication.class, args);
		System.out.println("x");
	}
}
