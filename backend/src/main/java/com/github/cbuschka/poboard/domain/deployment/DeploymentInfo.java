package com.github.cbuschka.poboard.domain.deployment;

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

	public static DeploymentInfo unreachable(String system, String env, String message)
	{
		return new DeploymentInfo(DeploymentStatus.UNREACHABLE, message, system, env, null, null, null);
	}

	public static DeploymentInfo unconfigured(String system, String env)
	{
		return new DeploymentInfo(DeploymentStatus.UNCONFIGURED, "Not configured.", system, env, null, null, null);
	}

	public static DeploymentInfo failure(String system, String env, String message)
	{
		return new DeploymentInfo(DeploymentStatus.FAILURE, "Failure.", system, env, null, null, null);
	}

	public static DeploymentInfo available(String system, String env, String commitish, String version, String branch)
	{
		return new DeploymentInfo(DeploymentStatus.AVAILABLE, "OK", system, env, commitish, version, branch);
	}
}
