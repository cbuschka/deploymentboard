package com.github.cbuschka.poboard.domain.deployment;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

@Component
public class HttpEndpointHandler implements EndpointHandler
{
	private final ObjectMapper objectMapper = new ObjectMapper();

	@Override
	public boolean handles(Endpoint endpoint)
	{
		return endpoint.getUrl().startsWith("http://") || endpoint.getUrl().startsWith("https://");
	}

	@Override
	public DeploymentInfo getDeploymentInfo(String system, String env, Endpoint endpoint)
	{
		try
		{
			URL url = new URL(endpoint.getUrl());
			HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
			httpConn.setDoInput(true);
			int responseCode = httpConn.getResponseCode();
			if (responseCode != 200)
			{
				return new DeploymentInfo(DeploymentStatus.UNAVAILABLE, system, env, null, null, null);
			}
			JsonNode jsonNode = this.objectMapper.reader().readTree(httpConn.getInputStream());
			if (jsonNode == null)
			{
				return new DeploymentInfo(DeploymentStatus.UNAVAILABLE, system, env, null, null, null);
			}
			String version = getStringFrom(jsonNode, "version");
			String commitish = getStringFrom(jsonNode, "commitish");
			String branch = getStringFrom(jsonNode, "branch");
			return new DeploymentInfo(DeploymentStatus.AVAILABLE, system, env, commitish, version, branch);
		}
		catch (
				IOException ex)

		{
			return new DeploymentInfo(DeploymentStatus.UNAVAILABLE, system, env, null, null, null);
		}

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
