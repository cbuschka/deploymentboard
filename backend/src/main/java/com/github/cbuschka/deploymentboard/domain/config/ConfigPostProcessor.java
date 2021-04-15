package com.github.cbuschka.deploymentboard.domain.config;

import com.github.cbuschka.deploymentboard.domain.deployment.Endpoint;
import com.github.cbuschka.deploymentboard.domain.deployment.Environment;
import com.github.cbuschka.deploymentboard.domain.deployment.System;
import com.github.cbuschka.deploymentboard.domain.scm.CodeRepository;
import com.github.cbuschka.deploymentboard.util.Collections;
import com.github.cbuschka.deploymentboard.util.Integers;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;

@Component
@Slf4j
public class ConfigPostProcessor
{
	@Autowired
	private Defaults defaults;

	public void process(Config config)
	{
		completeSettings(config);
		completeWorkspace(config);
		completeProjects(config);
		completeSystems(config);
		completeEnvironments(config);
		completeEndpoints(config);
		completeCredentials(config);
	}

	private void completeCredentials(Config config)
	{
		if (config.credentials == null)
		{
			config.credentials = new ArrayList<>();
		}
	}

	private void completeSystems(Config config)
	{
		if (config.systems == null)
		{
			config.systems = new ArrayList<>();
		}

		config.systems.forEach(this::completeSystem);
	}

	private void completeSystem(System system)
	{
		if (system.getEndpoints() == null)
		{
			system.setEndpoints(new HashMap<>());
		}
	}

	private void completeEnvironments(Config config)
	{
		if (config.environments == null || config.environments.isEmpty())
		{
			config.environments = new ArrayList<>(defaults.environments);
		}
	}

	private void completeProjects(Config config)
	{
		if (config.projects == null)
		{
			config.projects = new ArrayList<>();
		}
	}

	private void completeWorkspace(Config config)
	{
		if (config.workspace == null)
		{
			config.workspace = new Workspace();
		}
	}

	private void completeSettings(Config config)
	{
		if (config.settings == null)
		{
			config.settings = new Settings();
		}

		config.settings.setConnectTimeoutMillis(Integers.firstNonNull(config.settings.getConnectTimeoutMillis(), defaults.connectTimeoutMillis));
		config.settings.setReadTimeoutMillis(Integers.firstNonNull(config.settings.getReadTimeoutMillis(), defaults.readTimeoutMillis));
		config.settings.setRecheckTimeoutMillis(Integers.firstNonNull(config.settings.getRecheckTimeoutMillis(), defaults.recheckTimeoutMillis));
	}

	private void completeEndpoints(Config config)
	{
		for (System system : config.systems)
		{
			CodeRepository repository = system.getRepository();
			if (repository != null)
			{
				completeRepository(repository, config);
			}

			for (Environment environment : config.environments)
			{
				system.getEndpoint(environment.getName())
						.ifPresent(endpoint -> completeEndpoint(endpoint, config));
			}
		}
	}

	private void completeRepository(CodeRepository repository, Config config)
	{
		completeTimeouts(repository, config);
	}

	private void completeTimeouts(CodeRepository repository, Config config)
	{
		repository.setConnectTimeoutMillis(
				Integers.firstNonNull(
						repository.getConnectTimeoutMillis(),
						config.settings.getConnectTimeoutMillis(),
						defaults.connectTimeoutMillis));

	}

	private void completeEndpoint(Endpoint endpoint, Config config)
	{
		completeTimeouts(endpoint, config);
		completeAliases(endpoint, config);
	}

	private void completeAliases(Endpoint endpoint, Config config)
	{
		endpoint.setVersionAliases(Collections.combined(endpoint.getVersionAliases(),
				config.settings.getVersionAliases(),
				defaults.versionAliases));
		endpoint.setBranchAliases(Collections.combined(endpoint.getBranchAliases(),
				config.settings.getBranchAliases(),
				defaults.branchAliases));
		endpoint.setBuildTimestampAliases(Collections.combined(endpoint.getBuildTimestampAliases(),
				config.settings.getBuildTimestampAliases(),
				defaults.buildTimestampAliases));
		endpoint.setCommitishAliases(Collections.combined(endpoint.getCommitishAliases(),
				config.settings.getCommitishAliases(),
				defaults.commitishAliases));
	}

	private void completeTimeouts(Endpoint endpoint, Config config)
	{
		endpoint.setConnectTimeoutMillis(
				Integers.firstNonNull(
						endpoint.getConnectTimeoutMillis(),
						config.settings.getConnectTimeoutMillis(),
						defaults.connectTimeoutMillis));
		endpoint.setReadTimeoutMillis(
				Integers.firstNonNull(
						endpoint.getReadTimeoutMillis(),
						config.settings.getReadTimeoutMillis(),
						defaults.readTimeoutMillis));
		endpoint.setRecheckTimeoutMillis(
				Integers.firstNonNull(
						endpoint.getRecheckTimeoutMillis(),
						config.settings.getRecheckTimeoutMillis(),
						defaults.recheckTimeoutMillis));
	}
}
