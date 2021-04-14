package com.github.cbuschka.deploymentboard.domain.config;

import java.util.Arrays;
import java.util.List;

public class Defaults
{
	public final int reloadTimeoutMillis = 5_000;
	public final int connectTimeoutMillis = 3_000;
	public final int readTimeoutMillis = 3_000;
	public final int recheckTimeoutMillis = 5_000;
	public final List<String> buildTimestampAliases = Arrays.asList("timestamp", "build.timestamp", "buildTimestamp", "Build-Timestamp");
	public final List<String> versionAliases = Arrays.asList("version", "build.version", "project.version", "projectVersion", "buildVersion", "Project-Version");
	public final List<String> commitishAliases = Arrays.asList("commitish", "build.commitish", "comitish", "Build-Commitish");
	public final List<String> branchAliases = Arrays.asList("branch", "build.branch", "Build-Branch");
}
