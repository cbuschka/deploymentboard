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

		DashboardStateResponse dashboardStateResponse = DashboardStateResponse.newWithProducts("diva", "legacy")
				.withEnvironment("diva", new DashboardStateResponse.Environment("dev", "n/a"))
				.withEnvironment("diva", new DashboardStateResponse.Environment("fach", "n/a"))
				.withEnvironment("diva", new DashboardStateResponse.Environment("prod", "n/a"))
				.withEnvironment("legacy", new DashboardStateResponse.Environment("dev", "n/a"))
				.withEnvironment("legacy", new DashboardStateResponse.Environment("fach", "n/a"))
				.withEnvironment("legacy", new DashboardStateResponse.Environment("prod", "n/a"));
		return ResponseEntity.ok(dashboardStateResponse);
	}
}
