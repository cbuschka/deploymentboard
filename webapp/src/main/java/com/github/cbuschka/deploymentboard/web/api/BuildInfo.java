package com.github.cbuschka.deploymentboard.web.api;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

@Configuration
@PropertySource(value = "classpath:build-info.properties", ignoreResourceNotFound = true)
@Getter
public class BuildInfo
{
	@Value("${build-info.version:NOVERSION}")
	private String version;
	@Value("${build-info.commitish:NOCOMMITISH}")
	private String commitish;
	@Value("${build-info.branch:NOBRANCH}")
	private String branch;
	@Value("${build-info.timestamp:NOTIMESTAMP}")
	private String timestamp;
}
