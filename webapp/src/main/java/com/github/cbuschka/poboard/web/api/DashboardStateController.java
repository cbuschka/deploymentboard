package com.github.cbuschka.poboard.web.api;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.Collections;

@RestController
public class DashboardStateController
{
	@GetMapping(path = "/api/dashboard/state", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> getState()
	{
		DashboardStateResponse dashboardStateResponse = DashboardStateResponse.newWithEnvironments("prod", "stage", "dev")
				.withSystemEnvironment("prod", "ui", new DashboardStateResponse.SystemEnvironment("15.11.1", Collections.emptyList()))
				.withSystemEnvironment("prod", "backend", new DashboardStateResponse.SystemEnvironment("15.3.1", Collections.emptyList()))
				.withSystemEnvironment("stage", "ui", new DashboardStateResponse.SystemEnvironment("15.11.2-SNAPSHOT", Arrays.asList(new DashboardStateResponse.Issue("UI-13", DashboardStateResponse.IssueStatus.CLOSED), new DashboardStateResponse.Issue("UI-12", DashboardStateResponse.IssueStatus.OPEN))))
				.withSystemEnvironment("stage", "backend", new DashboardStateResponse.SystemEnvironment("15.3.2-SNAPSHOT", Arrays.asList(new DashboardStateResponse.Issue("BE-13", DashboardStateResponse.IssueStatus.CLOSED), new DashboardStateResponse.Issue("BE-7", DashboardStateResponse.IssueStatus.CLOSED))))
				.withSystemEnvironment("dev", "ui", new DashboardStateResponse.SystemEnvironment("15.11.2-SNAPSHOT", Arrays.asList(new DashboardStateResponse.Issue("UI-11", DashboardStateResponse.IssueStatus.OPEN))))
				.withSystemEnvironment("dev", "backend", new DashboardStateResponse.SystemEnvironment("15.3.2-SNAPSHOT", Arrays.asList(new DashboardStateResponse.Issue("BA-123", DashboardStateResponse.IssueStatus.OPEN),new DashboardStateResponse.Issue("BA-99", DashboardStateResponse.IssueStatus.MISSING))));
		return ResponseEntity.ok(dashboardStateResponse);
	}
}
