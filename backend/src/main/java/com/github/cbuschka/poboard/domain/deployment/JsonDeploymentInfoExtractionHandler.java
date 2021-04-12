package com.github.cbuschka.poboard.domain.deployment;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;

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

		String version = getStringFrom(jsonNode, "version");
		String commitish = getStringFrom(jsonNode, "commitish");
		String branch = getStringFrom(jsonNode, "branch");
		return new DeploymentInfo(DeploymentStatus.AVAILABLE, system, env, commitish, version, branch);
	}

	private String getStringFrom(JsonNode jsonNode, String path)
	{
		if (jsonNode == null)
		{
			return null;
		}

		JsonNode stringNode = jsonNode.at("/" + path);
		if (stringNode == null || !stringNode.isTextual())
		{
			return null;
		}
		return stringNode.asText();
	}
}
