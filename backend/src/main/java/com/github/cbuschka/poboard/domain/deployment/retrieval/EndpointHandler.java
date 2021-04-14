package com.github.cbuschka.poboard.domain.deployment.retrieval;

import com.github.cbuschka.poboard.domain.deployment.Endpoint;

public interface EndpointHandler
{
	boolean handles(Endpoint endpoint);

	byte[] getDeploymentInfo(String system, String env, Endpoint endpoint) throws Exception;
}
