package com.github.cbuschka.deploymentboard.domain.deployment;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class DeploymentInfo
{
	private DeploymentStatus status;
	private String message;
	private String system;
	private String env;
	private String commitish;
	private String version;
	private String branch;
	private String buildTimestamp;

	public static DeploymentInfo unconfigured(String system, String env, String message)
	{
		return new DeploymentInfo(DeploymentStatus.UNCONFIGURED, message, system, env, null, null, null, null);
	}

	public static DeploymentInfo failure(String system, String env, String message)
	{
		return new DeploymentInfo(DeploymentStatus.FAILURE, message, system, env, null, null, null, null);
	}

	public static DeploymentInfo available(String system, String env, String commitish, String version, String branch, String builtTimestamp)
	{
		return new DeploymentInfo(DeploymentStatus.OK, "OK", system, env, commitish, version, branch, builtTimestamp);
	}
}
