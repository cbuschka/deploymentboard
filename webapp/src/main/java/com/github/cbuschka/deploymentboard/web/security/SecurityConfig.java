package com.github.cbuschka.deploymentboard.web.security;

import com.github.cbuschka.deploymentboard.auth.LdapAuthenticationProvider;
import com.github.cbuschka.deploymentboard.auth.SimpleAuthenticationProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter
{
	@Override
	protected void configure(HttpSecurity http) throws Exception
	{
		http.csrf().disable()
				.authorizeRequests().anyRequest().permitAll();
	}

	@Bean
	@Override
	public AuthenticationManager authenticationManagerBean() throws Exception
	{
		return super.authenticationManagerBean();
	}

	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception
	{
		auth.authenticationProvider(ldapAuthenticationProvider())
				.authenticationProvider(simpleAuthenticationProvider());
	}

	@Bean
	protected SimpleAuthenticationProvider simpleAuthenticationProvider()
	{
		return new SimpleAuthenticationProvider();
	}

	@Bean
	protected LdapAuthenticationProvider ldapAuthenticationProvider()
	{
		return new LdapAuthenticationProvider();
	}
}
