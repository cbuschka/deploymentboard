package com.github.cbuschka.poboard.domain.deployment.retrieval;

import com.github.cbuschka.poboard.domain.deployment.Endpoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DeploymentInfoRetriever
{
	@Autowired
	private List<EndpointHandler> endpointHandlers;

	public byte[] extractDeploymentInfoFrom(String system, String env, Endpoint endpoint) throws Exception
	{
		for (EndpointHandler handler : endpointHandlers)
		{
			if (handler.handles(endpoint))
			{
				return handler.getDeploymentInfo(system, env, endpoint);
			}
		}

		throw new IllegalStateException("Unknown format.");
	}
}
