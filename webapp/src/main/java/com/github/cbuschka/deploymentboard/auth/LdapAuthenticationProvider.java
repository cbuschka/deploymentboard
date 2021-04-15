package com.github.cbuschka.deploymentboard.auth;

import com.github.cbuschka.deploymentboard.domain.config.Config;
import com.github.cbuschka.deploymentboard.domain.config.ConfigProvider;
import com.github.cbuschka.deploymentboard.domain.config.LdapAuthMethod;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AccountStatusException;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import java.util.Collections;
import java.util.Properties;

@Slf4j
public class LdapAuthenticationProvider implements AuthenticationProvider
{
	@Autowired
	private ConfigProvider configProvider;

	public Authentication authenticate(Authentication authentication) throws AuthenticationException
	{
		LdapAuthMethod ldapAuthMethod = getLdapAuthMethod();
		if (ldapAuthMethod == null)
		{
			return null;
		}

		if (!(authentication instanceof UsernamePasswordAuthenticationToken))
		{
			return null;
		}

		String username = getUsernameFrom(authentication);
		String password = getPasswordFrom(authentication);

		InitialDirContext dirContext = null;
		try
		{
			dirContext = connectToLdap(username, password, ldapAuthMethod);
			UserDetails userDetails = queryUser(username, password, dirContext, ldapAuthMethod);

			return new UsernamePasswordAuthenticationToken(userDetails, password, userDetails.getAuthorities());
		}
		catch (NamingException ex)
		{
			throw new AuthenticationServiceException("Authentication failed.", ex);
		}
		finally
		{
			if (dirContext != null)
			{
				closeQuietly(dirContext);
			}
		}
	}

	@Override
	public boolean supports(Class<?> authentication)
	{
		return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
	}

	private LdapAuthMethod getLdapAuthMethod()
	{
		Config config = configProvider.getConfig();
		if (!(config.auth instanceof LdapAuthMethod))
		{
			return null;
		}

		return (LdapAuthMethod) config.auth;
	}

	private UserDetails queryUser(String username, String password, InitialDirContext dirContext, LdapAuthMethod ldapAuthMethod) throws AccountStatusException, NamingException
	{
		SearchControls ctrls = new SearchControls();
		ctrls.setReturningAttributes(new String[]{});
		ctrls.setSearchScope(SearchControls.SUBTREE_SCOPE);
		NamingEnumeration<SearchResult> answers = dirContext.search(ldapAuthMethod.baseDn, String.format(ldapAuthMethod.userQuery, username), ctrls);
		if (!answers.hasMoreElements())
		{
			throw new InsufficientAuthenticationException("Cannot find user in directory.");
		}

		@SuppressWarnings("unused")
		javax.naming.directory.SearchResult result = answers.nextElement();
		UserDetails userDetails = new User(username, password, Collections.emptyList());
		return userDetails;
	}

	private void closeQuietly(InitialDirContext dirContext)
	{
		try
		{
			dirContext.close();
		}
		catch (NamingException ex)
		{
			log.warn("Closing ldap context failed.", ex);
		}
	}

	private String getPasswordFrom(Authentication authentication)
	{
		Object credentials = authentication.getCredentials();

		if (credentials == null)
		{
			throw new BadCredentialsException("Missing password.");
		}
		if (credentials instanceof String)
		{
			return (String) credentials;
		}
		else
		{
			throw new BadCredentialsException("Password not found.");
		}
	}

	private String getUsernameFrom(Authentication authentication)
	{
		String username = authentication.getName();
		if (username == null || username.isEmpty())
		{
			throw new BadCredentialsException("Missing username.");
		}
		return username.trim().toLowerCase().replaceAll("[%&?()]+", "_");
	}

	private InitialDirContext connectToLdap(String username, String password, LdapAuthMethod ldapAuthMethod) throws NamingException
	{
		Properties props = new Properties();
		props.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
		props.put(Context.PROVIDER_URL, ldapAuthMethod.url);
		props.put(Context.SECURITY_PRINCIPAL, String.format(ldapAuthMethod.bindDn, username));
		props.put(Context.SECURITY_CREDENTIALS, password);
		return new InitialDirContext(props);
	}

}
