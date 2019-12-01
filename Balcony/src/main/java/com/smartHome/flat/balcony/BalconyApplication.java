package com.smartHome.flat.balcony;

import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@EnableAutoConfiguration
@ComponentScan
@EnableScheduling
public class BalconyApplication {
	
	private static final Logger logger = LoggerFactory.getLogger(BalconyApplication.class);
	private static ConfigurableApplicationContext context;

	public static void main(String[] args) throws Exception {
		context = SpringApplication.run(BalconyApplication.class, args);
}
	
	public static void restart() {
		ApplicationArguments args = context.getBean(ApplicationArguments.class);

		Thread thread = new Thread(() -> {
			logger.info("Application started with command-line arguments: {}", Arrays.toString(args.getSourceArgs()));
			logger.info("NonOptionArgs: {}", args.getNonOptionArgs());
			logger.info("OptionNames: {}", args.getOptionNames());
			for (String name : args.getOptionNames()) {
				logger.info("arg-" + name + "=" + args.getOptionValues(name));
			}

			boolean containsOption = args.containsOption("person.name");
			logger.info("Contains person.name: " + containsOption);

			context.close();
			context = SpringApplication.run(BalconyApplication.class, args.getSourceArgs());
		});

		thread.setDaemon(false);
		thread.start();
	}

}
