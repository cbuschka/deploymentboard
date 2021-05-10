package com.github.cbuschka.deploymentboard.web.api;

import com.github.cbuschka.deploymentboard.business.issues.GetIssuesStateBusinessService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class IssuesController
{
	@Autowired
	private GetIssuesStateBusinessService getIssuesStateBusinessService;

	@GetMapping(path = "/api/issues/state", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> getState()
	{
		return ResponseEntity.ok(getIssuesStateBusinessService.getIssuesState());
	}
}
