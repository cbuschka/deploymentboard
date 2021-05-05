package com.github.cbuschka.deploymentboard.web.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class VersionController
{
	@Autowired
	private BuildInfo buildInfo;

	@GetMapping(value = "/api/version")
	public @ResponseBody
	ResponseEntity<Map<String, String>> getBuildInfo()
	{
		Map<String, String> versionInfo = new HashMap<>();
		versionInfo.put("version", buildInfo.getVersion());
		versionInfo.put("commitish", buildInfo.getCommitish());
		versionInfo.put("branch", buildInfo.getBranch());
		versionInfo.put("builtAt", buildInfo.getTimestamp());
		return ResponseEntity.ok(versionInfo);
	}
}
