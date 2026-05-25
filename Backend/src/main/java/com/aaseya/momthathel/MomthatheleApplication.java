package com.aaseya.momthathel;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import io.camunda.client.annotation.Deployment;

@SpringBootApplication
@Deployment(resources = "classpath:bpmn/*.bpmn")
public class MomthatheleApplication {

	public static void main(String[] args) {
		SpringApplication.run(MomthatheleApplication.class, args);
	}

}
