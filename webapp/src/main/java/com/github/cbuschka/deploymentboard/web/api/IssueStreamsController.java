package com.github.cbuschka.deploymentboard.web.api;

import com.github.cbuschka.deploymentboard.business.issueStreams.GetIssueStreamsStateBusinessService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class IssueStreamsController
{
	@Autowired
	private GetIssueStreamsStateBusinessService getIssueStreamsStateBusinessService;

	@GetMapping(path = "/api/issueStreams/state", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> getState()
	{
		return ResponseEntity.ok(getIssueStreamsStateBusinessService.getIssuesState());
	}
}
