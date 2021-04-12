package com.github.cbuschka.poboard.domain.deployment;

import com.github.cbuschka.poboard.domain.config.ConfigProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class EnvironmentDomainService
{
	private static final List<Environment> DEFAULT = Arrays.asList(new Environment("prod"), new Environment("stage"), new Environment("int"), new Environment("dev"));

	@Autowired
	private ConfigProvider configProvider;

	public List<String> getEnvironments()
	{
		return Optional.ofNullable(configProvider.getConfig().environments)
				.orElse(DEFAULT)
				.stream()
				.map(Environment::getName)
				.collect(Collectors.toList());
	}
}
