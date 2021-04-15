package com.github.cbuschka.deploymentboard.domain.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class ConfigLoader
{
	@Autowired
	private ConfigPostProcessor configPostProcessor;

	public Config loadConfig(Resource resource) throws IOException
	{
		Config config = new ObjectMapper(new YAMLFactory()).readerFor(Config.class).readValue(resource.getInputStream());

		this.configPostProcessor.process(config);

		return config;
	}
}
