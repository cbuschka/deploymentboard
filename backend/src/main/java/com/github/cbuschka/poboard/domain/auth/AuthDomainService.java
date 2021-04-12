package com.github.cbuschka.poboard.domain.auth;

import com.github.cbuschka.poboard.domain.mock.MockDataProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class AuthDomainService
{
	@Autowired
	private MockDataProvider mockDataProvider;

	public List<UsernamePasswordCredentials> getUsernamePasswordCredentials(String username, String hostname)
	{
		return getCredentials(hostname)
				.stream()
				.filter((c) -> c instanceof UsernamePasswordCredentials)
				.map((c) -> (UsernamePasswordCredentials) c)
				.filter((c) -> c.getUsername() != null && c.getUsername().equalsIgnoreCase(username))
				.collect(Collectors.toList());
	}

	public List<PrivateKeyCredentials> getPrivateKeyCredentials(String username, String hostname)
	{
		return getCredentials(hostname)
				.stream()
				.filter((c) -> c instanceof PrivateKeyCredentials)
				.map((c) -> (PrivateKeyCredentials) c)
				.filter((c) -> c.getUsername() != null && c.getUsername().equalsIgnoreCase(username))
				.collect(Collectors.toList());
	}

	public List<Credentials> getCredentials(String hostname)
	{
		return mockDataProvider.getMockData().credentials
				.stream()
				.filter((c) -> allowedForHostname(c, hostname))
				.collect(Collectors.toList());
	}

	private boolean allowedForHostname(Credentials credentials, String hostname)
	{
		Set<String> hostnames = credentials.getHostnames();
		if (hostnames == null || credentials.getHostnames().isEmpty())
		{
			return true;
		}

		for (String allowedHostname : hostnames)
		{
			if (Pattern.compile(allowedHostname, Pattern.CASE_INSENSITIVE).matcher(hostname).matches())
			{
				return true;
			}
		}

		return false;
	}
}
