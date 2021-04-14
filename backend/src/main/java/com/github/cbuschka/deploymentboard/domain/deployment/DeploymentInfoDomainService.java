package com.github.cbuschka.deploymentboard.domain.deployment;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class DeploymentInfoDomainService
{
	@Autowired
	private SystemDomainService systemDomainService;
	@Autowired
	private EnvironmentDomainService environmentDomainService;
	@Autowired
	private EndpointDomainService endpointDomainService;

	public Map<String, DeploymentInfo> getDeploymentInfosFor(System system)
	{
		Map<String, DeploymentInfo> deploymentInfos = new HashMap<>();
		for (Environment env : this.environmentDomainService.getEnvironments())
		{
			DeploymentInfo deploymentInfo = getDeploymentInfoFor(system, env);
			deploymentInfos.put(env.getName(), deploymentInfo);
		}

		return deploymentInfos;
	}

	public DeploymentInfo getDeploymentInfoFor(System system, Environment env)
	{
		return system
				.getEndpoint(env.getName())
				.map((endpoint) -> this.endpointDomainService.getDeploymentInfo(system.getName(), env.getName(), endpoint))
				.orElseGet(() -> DeploymentInfo.unconfigured(system.getName(), env.getName()));
	}
}
