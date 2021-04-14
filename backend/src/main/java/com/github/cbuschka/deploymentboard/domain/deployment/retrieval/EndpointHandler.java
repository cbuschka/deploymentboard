package com.github.cbuschka.deploymentboard.domain.deployment.retrieval;

import com.github.cbuschka.deploymentboard.domain.deployment.Endpoint;

public interface EndpointHandler
{
	boolean handles(Endpoint endpoint);

	byte[] getDeploymentInfo(String system, String env, Endpoint endpoint) throws Exception;
}
