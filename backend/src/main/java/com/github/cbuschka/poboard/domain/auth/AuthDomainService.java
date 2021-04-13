package com.github.cbuschka.poboard.domain.auth;

import com.github.cbuschka.poboard.domain.config.ConfigProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class AuthDomainService
{
	@Autowired
	private ConfigProvider configProvider;

	public List<PasswordCredentials> getUsernamePasswordCredentials(String username, String hostname)
	{
		return getCredentials(username, hostname)
				.stream()
				.filter((c) -> c instanceof PasswordCredentials)
				.map((c) -> (PasswordCredentials) c)
				.collect(Collectors.toList());
	}

	public List<PrivateKeyCredentials> getPrivateKeyCredentials(String username, String hostname)
	{
		return getCredentials(username, hostname)
				.stream()
				.filter((c) -> c instanceof PrivateKeyCredentials)
				.map((c) -> (PrivateKeyCredentials) c)
				.collect(Collectors.toList());
	}

	public List<Credentials> getCredentials(String optionalUsername, String hostname)
	{
		return Optional.ofNullable(configProvider.getConfig().credentials)
				.orElseGet(Collections::emptyList)
				.stream()
				.filter((c) -> allowedFor(hostname, c.getAllowedHostnames()))
				.filter((c) -> allowedFor(optionalUsername, c.getAllowedUsernames()))
				.collect(Collectors.toList());
	}

	private boolean allowedFor(String name, Set<String> allowedNamesSet)
	{
		if (allowedNamesSet == null || allowedNamesSet.isEmpty())
		{
			return true;
		}

		if (name == null)
		{
			return false;
		}

		for (String allowedHostname : allowedNamesSet)
		{
			if (Pattern.compile(allowedHostname, Pattern.CASE_INSENSITIVE).matcher(name).matches())
			{
				return true;
			}
		}

		return false;
	}
}
