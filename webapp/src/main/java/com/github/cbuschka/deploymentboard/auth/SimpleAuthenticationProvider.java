package com.github.cbuschka.deploymentboard.auth;

import com.github.cbuschka.deploymentboard.domain.config.Config;
import com.github.cbuschka.deploymentboard.domain.config.ConfigProvider;
import com.github.cbuschka.deploymentboard.domain.config.SimpleAuthMethod;
import com.github.cbuschka.deploymentboard.domain.config.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collections;

public class SimpleAuthenticationProvider implements AuthenticationProvider
{
	@Autowired
	private ConfigProvider configProvider;

	public Authentication authenticate(Authentication authentication) throws AuthenticationException
	{
		SimpleAuthMethod authMethod = getSimpleAuthMethod();
		if (authMethod == null)
		{
			return null;
		}

		if (!(authentication instanceof UsernamePasswordAuthenticationToken))
		{
			return null;
		}

		for (User user : authMethod.users)
		{
			if (user.name != null && user.name.equalsIgnoreCase(authentication.getName())
					&& user.password != null && user.password.equals(authentication.getCredentials()))
			{
				UserDetails userDetails = new org.springframework.security.core.userdetails.User(user.name, user.password, Collections.emptyList());
				return new UsernamePasswordAuthenticationToken(userDetails, user.password, userDetails.getAuthorities());
			}
		}

		throw new BadCredentialsException("Bad credentials.");
	}

	private SimpleAuthMethod getSimpleAuthMethod()
	{
		Config config = configProvider.getConfig();
		if (config.auth instanceof SimpleAuthMethod)
		{
			return (SimpleAuthMethod) config.auth;
		}

		return null;
	}

	@Override
	public boolean supports(Class<?> authentication)
	{
		return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
	}
}
