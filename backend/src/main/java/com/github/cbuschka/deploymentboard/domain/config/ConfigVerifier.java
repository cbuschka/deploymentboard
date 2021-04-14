package com.github.cbuschka.deploymentboard.domain.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ConfigVerifier
{
	public void verify(Config config)
	{
		if (!"1.0".equals(config.version))
		{
			throw new IllegalArgumentException("Expected config version \"1.0\".");
		}

		if (config.workspace == null || config.workspace.getDir() == null)
		{
			throw new IllegalArgumentException("No workspace.dir set.");
		}

		if (config.projects == null || config.projects.isEmpty())
		{
			log.warn("No projects set in config. Issue status will not be avaialble.");
		}

		if (config.systems == null || config.systems.isEmpty())
		{
			log.warn("No systems set in config. Without systems the dashboard does not really make sense.");
		}

		if (config.environments == null || config.environments.isEmpty())
		{
			log.warn("No environments set in config. Defaults will be used.");
		}
	}
}
