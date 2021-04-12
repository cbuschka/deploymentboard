package com.github.cbuschka.poboard.domain.deployment;


import com.github.cbuschka.poboard.domain.config.ConfigProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SystemDomainService
{
	@Autowired
	private ConfigProvider configProvider;

	public List<System> getSystems()
	{
		return this.configProvider.getConfig().systems;
	}
}
