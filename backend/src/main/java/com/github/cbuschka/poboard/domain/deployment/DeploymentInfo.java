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
	private String system;
	private String env;
	private String commitish;
	private String version;
	private String branch;

	public static DeploymentInfo unvailable(String system, String env)
	{
		return new DeploymentInfo(DeploymentStatus.UNAVAILABLE, system, env, null, null, null);
	}
}
