package com.github.cbuschka.poboard.domain.deployment;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@Component
public class YamlDeploymentInfoExtractionHandler implements DeploymentInfoExtractionHandler
{
	private final ObjectMapper objectMapper = new ObjectMapper(new YAMLFactory());

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

		String version = getStringFrom(jsonNode, DeploymentInfoExtractor.VERSION_ALIASES);
		String commitish = getStringFrom(jsonNode, DeploymentInfoExtractor.COMMITISH_ALIASES);
		String branch = getStringFrom(jsonNode, DeploymentInfoExtractor.BRANCH_ALIASES);
		return DeploymentInfo.available(system, env, commitish, version, branch);
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
