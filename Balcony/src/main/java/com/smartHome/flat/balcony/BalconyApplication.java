package com.smartHome.flat.balcony;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@EnableAutoConfiguration
@ComponentScan
@EnableScheduling
public class BalconyApplication {

	public static void main(String[] args) throws Exception {
		SpringApplication.run(BalconyApplication.class, args);
	}

}
