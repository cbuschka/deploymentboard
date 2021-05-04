package com.github.cbuschka.deploymentboard.web.api;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class VersionController
{
	@Value("${build-info.version:NOVERSION}")
	private String buildInfoVersion;
	@Value("${build-info.commitish:NOCOMMITISH}")
	private String buildInfoCommitish;
	@Value("${build-info.branch:NOBRANCH}")
	private String buildInfoBranch;
	@Value("${build-info.timestamp:NOTIMESTAMP}")
	private String buildInfoTimestamp;

	@GetMapping(value = "/api/version")
	public @ResponseBody
	ResponseEntity<Map<String, String>> getBuildInfo()
	{
		Map<String, String> versionInfo = new HashMap<>();
		versionInfo.put("version", buildInfoVersion);
		versionInfo.put("commitish", buildInfoCommitish);
		versionInfo.put("branch", buildInfoBranch);
		versionInfo.put("builtAt", buildInfoTimestamp);
		return ResponseEntity.ok(versionInfo);
	}
}
