package com.github.cbuschka.poboard.web.api;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DashboardStateController
{
	@GetMapping(path="/api/dashboard/state", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> getState() {

		DashboardStateResponse dashboardStateResponse = DashboardStateResponse.newWithEnvironments("dev","stage","prod")
				.withSystemEnvironment("dev", "ui", new DashboardStateResponse.SystemEnvironment("n/a"))
				.withSystemEnvironment("stage", "ui", new DashboardStateResponse.SystemEnvironment("n/a"))
				.withSystemEnvironment("prod", "ui", new DashboardStateResponse.SystemEnvironment("n/a"))
				.withSystemEnvironment("dev", "backend", new DashboardStateResponse.SystemEnvironment("n/a"))
				.withSystemEnvironment("stage", "backend", new DashboardStateResponse.SystemEnvironment("n/a"))
				.withSystemEnvironment("prod", "backend", new DashboardStateResponse.SystemEnvironment("n/a"));
		return ResponseEntity.ok(dashboardStateResponse);
	}
}
