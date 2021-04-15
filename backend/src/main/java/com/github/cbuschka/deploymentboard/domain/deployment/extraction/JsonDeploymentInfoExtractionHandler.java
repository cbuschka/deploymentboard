package com.github.cbuschka.deploymentboard.domain.deployment.extraction;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.cbuschka.deploymentboard.domain.deployment.DeploymentInfo;
import com.github.cbuschka.deploymentboard.domain.deployment.Endpoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.util.Set;

@Component
public class JsonDeploymentInfoExtractionHandler implements DeploymentInfoExtractionHandler
{
	private final ObjectMapper objectMapper = new ObjectMapper();

	@Override
	public boolean handles(Endpoint endpoint)
	{
		return "json".equals(endpoint.getFormat()) || (endpoint.getUrl() != null && endpoint.getUrl().endsWith(".json"));
	}

	@Override
	public DeploymentInfo extractDeploymentInfoFrom(Endpoint endpoint, InputStream in, String system, String env) throws IOException
	{
		JsonNode jsonNode = this.objectMapper.reader().readTree(in);
		if (jsonNode == null)
		{
			return DeploymentInfo.failure(system, env, "No json.");
		}

		String version = getStringFrom(jsonNode, endpoint.getVersionAliases());
		String commitish = getStringFrom(jsonNode, endpoint.getCommitishAliases());
		String branch = getStringFrom(jsonNode, endpoint.getBranchAliases());
		String buildTimestamp = getStringFrom(jsonNode, endpoint.getBuildTimestampAliases());
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
