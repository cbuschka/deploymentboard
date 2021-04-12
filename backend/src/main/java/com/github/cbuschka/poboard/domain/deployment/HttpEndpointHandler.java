package com.github.cbuschka.poboard.domain.deployment;

import com.github.cbuschka.poboard.domain.auth.AuthDomainService;
import com.github.cbuschka.poboard.domain.auth.PasswordCredentials;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;

@Component
@Slf4j
public class HttpEndpointHandler implements EndpointHandler
{
	@Autowired
	private AuthDomainService authDomainService;
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
			httpConn.setConnectTimeout(3_000);
			httpConn.setReadTimeout(10_000);
			addBasicAuthHeaderIfAvailable(url, httpConn);
			httpConn.setDoInput(true);
			int responseCode = httpConn.getResponseCode();
			if (responseCode != 200)
			{
				return DeploymentInfo.unvailable(system, env);
			}

			return deploymentInfoExtractor.extractDeploymentInfoFrom(httpConn.getInputStream(), system, env, endpoint);
		}
		catch (IOException ex)
		{
			log.error("Getting deployment info for {} failed.", endpoint.getUrl(), ex);

			return DeploymentInfo.unvailable(system, env);
		}

	}

	private void addBasicAuthHeaderIfAvailable(URL url, HttpURLConnection httpConn)
	{
		if (url.getUserInfo() == null)
		{
			return;
		}

		List<PasswordCredentials> usernamePasswordCredentials = this.authDomainService.getUsernamePasswordCredentials(url.getUserInfo(), url.getHost());
		if (usernamePasswordCredentials.isEmpty())
		{
			return;
		}

		if (usernamePasswordCredentials.size() > 1)
		{
			log.warn("Multiple password credentials avaialble for {}. Please fix.", url);
		}

		String usernameColonPassword = String.format("%s:%s", url.getUserInfo(), usernamePasswordCredentials.get(0).getPassword());
		String authHeader = "Basic " + Base64.getEncoder().encodeToString(usernameColonPassword.getBytes(StandardCharsets.UTF_8));
		httpConn.addRequestProperty("Authentication", authHeader);
	}
}
