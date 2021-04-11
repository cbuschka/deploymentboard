package com.github.cbuschka.poboard.domain.deployment;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

@Service
public class EndpointDomainService
{
	private ObjectMapper objectMapper = new ObjectMapper();

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
		catch (IOException ex)
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
