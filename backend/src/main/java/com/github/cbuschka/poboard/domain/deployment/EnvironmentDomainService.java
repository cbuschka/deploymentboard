package com.github.cbuschka.poboard.domain.deployment;

import com.github.cbuschka.poboard.domain.config.ConfigProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class EnvironmentDomainService
{
	@Autowired
	private ConfigProvider configProvider;

	public List<String> getEnvironments()
	{
		return configProvider.getConfig().environments.stream().map(Environment::getName).collect(Collectors.toList());
	}
}
