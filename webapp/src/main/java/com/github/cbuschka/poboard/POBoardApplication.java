package com.github.cbuschka.poboard;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;

@SpringBootApplication
@PropertySource(value="classpath:backend-application.properties", ignoreResourceNotFound = false)
@PropertySource(value="classpath:webapp-application.properties", ignoreResourceNotFound = false)
public class POBoardApplication
{
	public static void main(String[] args)
	{
		SpringApplication.run(POBoardApplication.class, args);
	}
}
