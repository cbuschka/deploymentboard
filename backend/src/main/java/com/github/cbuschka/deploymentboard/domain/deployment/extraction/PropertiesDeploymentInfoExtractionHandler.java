package com.github.cbuschka.deploymentboard.domain.deployment.extraction;

import com.github.cbuschka.deploymentboard.domain.config.Config;
import com.github.cbuschka.deploymentboard.domain.config.ConfigProvider;
import com.github.cbuschka.deploymentboard.domain.deployment.DeploymentInfo;
import com.github.cbuschka.deploymentboard.domain.deployment.Endpoint;
import com.github.cbuschka.deploymentboard.util.Collections;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;
import java.util.Set;

@Component
public class PropertiesDeploymentInfoExtractionHandler implements DeploymentInfoExtractionHandler
{
	@Autowired
	private ConfigProvider configProvider;

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

		Config config = configProvider.getConfig();
		String version = getStringFrom(properties, Collections.combined(config.settings.getVersionAliases(), config.defaults.versionAliases));
		String commitish = getStringFrom(properties, Collections.combined(config.settings.getCommitishAliases(), config.defaults.commitishAliases));
		String branch = getStringFrom(properties, Collections.combined(config.settings.getBranchAliases(), config.defaults.branchAliases));
		String buildTimestamp = getStringFrom(properties, Collections.combined(config.settings.getBuildTimestampAliases(), config.defaults.buildTimestampAliases));
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
