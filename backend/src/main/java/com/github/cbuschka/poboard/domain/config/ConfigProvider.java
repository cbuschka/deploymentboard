package com.github.cbuschka.poboard.domain.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class ConfigProvider
{
	@Value("classpath:config.yaml")
	private Resource configResource;

	public Config getConfig()
	{
		try
		{
			return new ObjectMapper(new YAMLFactory()).readerFor(Config.class).readValue(this.configResource.getInputStream());
		}
		catch (IOException ex)
		{
			throw new RuntimeException(ex);
		}
	}
}
