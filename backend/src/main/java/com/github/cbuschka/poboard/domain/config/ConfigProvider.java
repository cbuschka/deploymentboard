package com.github.cbuschka.poboard.domain.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;

@Component
@Slf4j
public class ConfigProvider
{
	@Value("${poboard.config:classpath:empty-config.yaml}")
	private Resource configResource;

	@Autowired
	private ConfigVerifier configVerifier;

	private long configLastModified;
	private Config config;

	@PostConstruct
	public synchronized Config getConfig()
	{
		if (mustLoad())
		{
			loadConfig();
		}

		return this.config;
	}

	private boolean mustLoad()
	{
		try
		{
			return this.config == null || this.configResource.lastModified() != this.configLastModified;
		}
		catch (IOException ex)
		{
			log.warn("Reload check failed.", ex);
			return false;
		}
	}

	private void loadConfig()
	{
		try
		{
			Config config = new ObjectMapper(new YAMLFactory()).readerFor(Config.class).readValue(this.configResource.getInputStream());

			log.info("Config loaded from {}.", this.configResource.getURI());

			this.configVerifier.verify(config);

			this.configLastModified = this.configResource.lastModified();
			this.config = config;
		}
		catch (IOException ex)
		{
			throw new RuntimeException(ex);
		}
	}
}
