package com.github.cbuschka.poboard.domain.deployment;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

@Component
public class DeploymentInfoExtractor
{
	final static List<String> VERSION_ALIASES = Arrays.asList("version", "build.version", "project.version", "projectVersion", "buildVersion", "Project-Version");
	final static List<String> COMMITISH_ALIASES = Arrays.asList("commitish", "build.commitish", "comitish", "Build-Commitish");
	final static List<String> BRANCH_ALIASES = Arrays.asList("branch", "build.branch", "Build-Branch");

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
