package com.github.cbuschka.poboard.domain.deployment;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

@Component
@Slf4j
public class HttpEndpointHandler implements EndpointHandler
{
	@Autowired
	private DeploymentInfoExtractor deploymentInfoExtractor;

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

			return deploymentInfoExtractor.extractDeploymentInfoFrom(httpConn.getInputStream(), system, env);
		}
		catch (IOException ex)
		{
			log.error("Getting deployment info for {} failed.", endpoint.getUrl(), ex);

			return new DeploymentInfo(DeploymentStatus.UNAVAILABLE, system, env, null, null, null);
		}

	}
}
