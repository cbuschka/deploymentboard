package com.github.cbuschka.deploymentboard.domain.deployment.retrieval;

import com.github.cbuschka.deploymentboard.domain.deployment.Endpoint;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class DeploymentInfoRetriever
{
	@Autowired
	private List<EndpointHandler> endpointHandlers;

	public byte[] retrieveDeploymentInfoFrom(String system, String env, Endpoint endpoint) throws Exception
	{
		byte[] bytes = retrieveDeploymentInfoInternal(system, env, endpoint);
		log.info("Got deployment info for {}/{} from {}.", system, env, endpoint);
		return bytes;
	}

	private byte[] retrieveDeploymentInfoInternal(String system, String env, Endpoint endpoint) throws Exception
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
