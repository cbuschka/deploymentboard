package com.github.cbuschka.deploymentboard.domain.deployment.extraction;

import com.github.cbuschka.deploymentboard.domain.config.ConfigProvider;
import com.github.cbuschka.deploymentboard.domain.deployment.DeploymentInfo;
import com.github.cbuschka.deploymentboard.domain.deployment.Endpoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

@Component
public class DeploymentInfoExtractor
{

	@Autowired
	private List<DeploymentInfoExtractionHandler> deploymentInfoExtractionHandlers;

	public DeploymentInfo extractDeploymentInfoFrom(InputStream in, String system, String env, Endpoint endpoint) throws IOException
	{
		for (DeploymentInfoExtractionHandler handler : deploymentInfoExtractionHandlers)
		{
			if (handler.handles(endpoint))
			{
				return handler.extractDeploymentInfoFrom(endpoint, in, system, env);
			}
		}

		return DeploymentInfo.failure(system, env, "Unknown format.");
	}
}
