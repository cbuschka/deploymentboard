package com.github.cbuschka.deploymentboard.web.api;

import com.github.cbuschka.deploymentboard.business.dashboard.GetDashboardStateBusinessService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DashboardStateController
{
	@Autowired
	private GetDashboardStateBusinessService getDashboardStateBusinessService;

	@GetMapping(path = "/api/dashboard/state", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> getState()
	{
		return ResponseEntity.ok(getDashboardStateBusinessService.getDashboardState());
	}
}
