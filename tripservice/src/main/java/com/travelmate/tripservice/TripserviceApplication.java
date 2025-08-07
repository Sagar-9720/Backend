package com.travelmate.tripservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class TripserviceApplication {

	public static void main(String[] args) {
		SpringApplication.run(TripserviceApplication.class, args);
	}

}
