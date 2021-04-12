package com.github.cbuschka.poboard.domain.deployment;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EndpointDomainService
{
	@Autowired
	private List<EndpointHandler> endpointHandlers;

	public DeploymentInfo getDeploymentInfo(String system, String env, Endpoint endpoint)
	{
		for (EndpointHandler handler : this.endpointHandlers)
		{
			if (handler.handles(endpoint))
			{
				return handler.getDeploymentInfo(system, env, endpoint);
			}
		}

		return DeploymentInfo.unvailable(system, env);
	}
}
