package com.github.cbuschka.deploymentboard.domain.deployment.extraction;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
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
import java.util.Set;

@Component
public class YamlDeploymentInfoExtractionHandler implements DeploymentInfoExtractionHandler
{
	private final ObjectMapper objectMapper = new ObjectMapper(new YAMLFactory());

	@Autowired
	private ConfigProvider configProvider;

	@Override
	public boolean handles(Endpoint endpoint)
	{
		return "yaml".equals(endpoint.getFormat()) || (endpoint.getUrl() != null && (endpoint.getUrl().endsWith(".yaml") || endpoint.getUrl().endsWith(".yml")));
	}

	@Override
	public DeploymentInfo extractDeploymentInfoFrom(Endpoint endpoint, InputStream in, String system, String env) throws IOException
	{
		JsonNode jsonNode = this.objectMapper.reader().readTree(in);
		if (jsonNode == null)
		{
			return DeploymentInfo.failure(system, env, "No yaml.");
		}

		Config config = configProvider.getConfig();
		String version = getStringFrom(jsonNode, Collections.combined(config.settings.getVersionAliases(), config.defaults.versionAliases));
		String commitish = getStringFrom(jsonNode, Collections.combined(config.settings.getCommitishAliases(), config.defaults.commitishAliases));
		String branch = getStringFrom(jsonNode, Collections.combined(config.settings.getBranchAliases(), config.defaults.branchAliases));
		String buildTimestamp = getStringFrom(jsonNode, Collections.combined(config.settings.getBuildTimestampAliases(), config.defaults.buildTimestampAliases));
		return DeploymentInfo.available(system, env, commitish, version, branch, buildTimestamp);
	}

	private String getStringFrom(JsonNode jsonNode, Set<String> keys)
	{
		for (String key : keys)
		{
			JsonNode stringNode = jsonNode.at("/" + key);
			if (stringNode != null && stringNode.isTextual())
			{
				return stringNode.asText();
			}
		}

		return null;
	}
}
