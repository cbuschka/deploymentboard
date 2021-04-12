package com.github.cbuschka.poboard.domain.deployment;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;

@Component
public class DeploymentInfoExtractor
{
	private final ObjectMapper objectMapper = new ObjectMapper();

	public DeploymentInfo extractDeploymentInfoFrom(InputStream in, String system, String env) throws IOException
	{
		JsonNode jsonNode = this.objectMapper.reader().readTree(in);
		if (jsonNode == null)
		{
			return new DeploymentInfo(DeploymentStatus.UNAVAILABLE, system, env, null, null, null);
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
