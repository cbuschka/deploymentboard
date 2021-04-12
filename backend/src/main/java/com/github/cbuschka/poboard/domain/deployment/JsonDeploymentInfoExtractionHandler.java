package com.github.cbuschka.poboard.domain.deployment;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@Component
public class JsonDeploymentInfoExtractionHandler implements DeploymentInfoExtractionHandler
{
	private final ObjectMapper objectMapper = new ObjectMapper();

	@Override
	public boolean handles(Endpoint endpoint)
	{
		return endpoint.getUrl() != null && endpoint.getUrl().endsWith(".json");
	}

	@Override
	public DeploymentInfo extractDeploymentInfoFrom(Endpoint endpoint, InputStream in, String system, String env) throws IOException
	{
		JsonNode jsonNode = this.objectMapper.reader().readTree(in);
		if (jsonNode == null)
		{
			return DeploymentInfo.unvailable(system, env);
		}

		String version = getStringFrom(jsonNode, DeploymentInfoExtractor.VERSION_ALIASES);
		String commitish = getStringFrom(jsonNode, DeploymentInfoExtractor.COMMITISH_ALIASES);
		String branch = getStringFrom(jsonNode, DeploymentInfoExtractor.BRANCH_ALIASES);
		return new DeploymentInfo(DeploymentStatus.AVAILABLE, system, env, commitish, version, branch);
	}

	private String getStringFrom(JsonNode jsonNode, List<String> keys)
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
