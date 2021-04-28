package com.github.cbuschka.deploymentboard.domain.issue_tracking;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.cbuschka.deploymentboard.domain.auth.PasswordCredentials;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Optional;

public class JiraClient
{
	private static ObjectMapper objectMapper = new ObjectMapper().disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

	private JiraIssueStatusMapper jiraIssueStatusMapper = new JiraIssueStatusMapper();

	private int connectTimeoutMillis = 10_000;
	private int readTimeoutMillis = 30_000;

	public Optional<IssueInfo> getIssueInfo(String baseUrl, String issueNo, String username, PasswordCredentials credentials) throws IOException
	{
		URL url = new URL(String.format("%s/rest/api/2/issue/%s?fields=summary,status", baseUrl, URLEncoder.encode(issueNo, StandardCharsets.UTF_8)));
		return fetch(url, username, credentials)
				.map((r) -> toIssueInfo(baseUrl, r));
	}

	private IssueInfo toIssueInfo(String baseUrl, JiraGetIssueResponse response)
	{
		String browseUrl = String.format("%s/browse/%s", baseUrl, URLEncoder.encode(response.key, StandardCharsets.UTF_8));
		return new IssueInfo(response.key, response.fields != null ? this.jiraIssueStatusMapper.map(response.fields.status) : IssueStatus.UNKNOWN,
				response.fields != null ? response.fields.summary : null, browseUrl);
	}

	private Optional<JiraGetIssueResponse> fetch(URL url, String username, PasswordCredentials passwordCredentials) throws IOException
	{
		HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
		httpConn.setConnectTimeout(this.connectTimeoutMillis);
		httpConn.setReadTimeout(this.readTimeoutMillis);
		httpConn.setRequestMethod("GET");
		addBasicAuthHeaderIfAvailable(httpConn, username, passwordCredentials);
		httpConn.addRequestProperty("Accept", "application/json");
		httpConn.setDoInput(true);
		httpConn.setDoOutput(false);
		int responseCode = httpConn.getResponseCode();
		if (responseCode == 404)
		{
			return Optional.empty();
		}
		if (responseCode != 200)
		{
			throw new IOException("Response code: " + responseCode);
		}

		byte[] json = httpConn.getInputStream().readAllBytes();
		JiraGetIssueResponse response = objectMapper.readerFor(JiraGetIssueResponse.class).readValue(json);
		return Optional.of(response);
	}

	private void addBasicAuthHeaderIfAvailable(HttpURLConnection httpConn, String username, PasswordCredentials passwordCredentials)
	{
		if (username == null || passwordCredentials == null)
		{
			return;
		}

		String usernameColonPassword = String.format("%s:%s", username, passwordCredentials.getPassword());
		String authHeader = "Basic " + Base64.getEncoder().encodeToString(usernameColonPassword.getBytes(StandardCharsets.UTF_8));
		httpConn.addRequestProperty("Authorization", authHeader);
	}
}
