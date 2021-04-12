package com.github.cbuschka.poboard.domain.deployment;

public interface EndpointHandler
{
	boolean handles(Endpoint endpoint);

	DeploymentInfo getDeploymentInfo(String system, String env, Endpoint endpoint);
}
