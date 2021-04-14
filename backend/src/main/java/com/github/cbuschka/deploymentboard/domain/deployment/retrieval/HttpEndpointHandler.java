package com.github.cbuschka.deploymentboard.domain.deployment.retrieval;

import com.github.cbuschka.deploymentboard.domain.auth.AuthDomainService;
import com.github.cbuschka.deploymentboard.domain.auth.PasswordCredentials;
import com.github.cbuschka.deploymentboard.domain.config.Config;
import com.github.cbuschka.deploymentboard.domain.config.ConfigProvider;
import com.github.cbuschka.deploymentboard.domain.deployment.Endpoint;
import com.github.cbuschka.deploymentboard.util.Integers;
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
	private ConfigProvider configProvider;

	@Override
	public boolean handles(Endpoint endpoint)
	{
		return endpoint.getUrl().startsWith("http://") || endpoint.getUrl().startsWith("https://");
	}

	@Override
	public byte[] getDeploymentInfo(String system, String env, Endpoint endpoint) throws IOException
	{
		Config config = configProvider.getConfig();

		URL url = new URL(endpoint.getUrl());
		HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
		httpConn.setConnectTimeout(Integers.firstNonNull(endpoint.getConnectTimeoutMillis(), config.settings.getConnectTimeoutMillis(), config.defaults.connectTimeoutMillis));
		httpConn.setReadTimeout(Integers.firstNonNull(endpoint.getReadTimeoutMillis(), config.settings.getReadTimeoutMillis(), config.defaults.readTimeoutMillis));
		addBasicAuthHeaderIfAvailable(url, httpConn);
		httpConn.setDoInput(true);
		int responseCode = httpConn.getResponseCode();
		if (responseCode != 200)
		{
			throw new IOException("Response code: " + responseCode);
		}

		return httpConn.getInputStream().readAllBytes();
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
