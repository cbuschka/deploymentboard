package com.github.cbuschka.deploymentboard.domain.config;

import com.github.cbuschka.deploymentboard.domain.deployment.Environment;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class Defaults
{
	public final int connectTimeoutMillis = 3_000;
	public final int readTimeoutMillis = 3_000;
	public final int recheckTimeoutMillis = 5_000;
	public final List<String> buildTimestampAliases = Arrays.asList("timestamp", "build.timestamp", "buildTimestamp", "Build-Timestamp");
	public final List<String> versionAliases = Arrays.asList("version", "build.version", "project.version", "projectVersion", "buildVersion", "Project-Version");
	public final List<String> commitishAliases = Arrays.asList("commitish", "build.commitish", "comitish", "Build-Commitish");
	public final List<String> branchAliases = Arrays.asList("branch", "build.branch", "Build-Branch");
	public final List<Environment> environments = Stream.of("prod", "stage", "int", "dev").map(Environment::new).collect(Collectors.toList());
}
