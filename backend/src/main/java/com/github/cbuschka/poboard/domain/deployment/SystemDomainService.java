package com.github.cbuschka.poboard.domain.deployment;


import com.github.cbuschka.poboard.domain.config.ConfigProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class SystemDomainService
{
	@Autowired
	private ConfigProvider configProvider;

	public List<System> getSystems()
	{
		return Optional.ofNullable(this.configProvider.getConfig().systems)
				.orElseGet(Collections::emptyList);
	}
}
