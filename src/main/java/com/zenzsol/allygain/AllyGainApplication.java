package com.zenzsol.allygain;

import java.util.logging.Logger;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;



//@SpringBootApplication(exclude = { SecurityAutoConfiguration.class })
@SpringBootApplication
public class AllyGainApplication {
	
	private static final Logger log = Logger.getLogger(AllyGainApplication.class
			.getName());

	public static void main(String[] args) {
		log.info("spring application start");
		SpringApplication.run(AllyGainApplication.class, args);
	}


}
