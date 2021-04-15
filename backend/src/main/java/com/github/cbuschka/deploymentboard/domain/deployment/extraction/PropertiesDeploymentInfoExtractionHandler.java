package com.github.cbuschka.deploymentboard.domain.deployment.extraction;

import com.github.cbuschka.deploymentboard.domain.deployment.DeploymentInfo;
import com.github.cbuschka.deploymentboard.domain.deployment.Endpoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.Set;

@Component
public class PropertiesDeploymentInfoExtractionHandler implements DeploymentInfoExtractionHandler
{
	@Override
	public boolean handles(Endpoint endpoint)
	{
		return "properties".equals(endpoint.getFormat()) || (endpoint.getUrl() != null && endpoint.getUrl().endsWith(".properties"));
	}

	@Override
	public DeploymentInfo extractDeploymentInfoFrom(Endpoint endpoint, InputStream in, String system, String env) throws IOException
	{
		Properties properties = new Properties();
		properties.load(in);

		String version = getStringFrom(properties, endpoint.getVersionAliases());
		String commitish = getStringFrom(properties, endpoint.getCommitishAliases());
		String branch = getStringFrom(properties, endpoint.getBranchAliases());
		String buildTimestamp = getStringFrom(properties, endpoint.getBuildTimestampAliases());
		return DeploymentInfo.available(system, env, commitish, version, branch, buildTimestamp);
	}

	private String getStringFrom(Properties properties, Set<String> keys)
	{
		for (String key : keys)
		{
			String value = properties.getProperty(key);
			if (value != null)
			{
				return value;
			}
		}

		return null;
	}
}
