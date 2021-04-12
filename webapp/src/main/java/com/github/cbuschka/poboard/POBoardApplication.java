package com.github.cbuschka.poboard;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;

@SpringBootApplication
public class POBoardApplication
{
	public static void main(String[] args)
	{
		SpringApplication.run(POBoardApplication.class, args);
	}
}
