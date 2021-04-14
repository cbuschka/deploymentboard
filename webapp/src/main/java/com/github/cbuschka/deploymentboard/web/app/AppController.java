package com.github.cbuschka.deploymentboard.web.app;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AppController
{
	@GetMapping(path = {"/app/{rest:.*}"})
	public String getApp()
	{
		return "/index.html";
	}
}
