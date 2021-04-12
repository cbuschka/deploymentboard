package com.github.cbuschka.poboard.domain.deployment;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

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
		for (String env : this.environmentDomainService.getEnvironments())
		{
			DeploymentInfo deploymentInfo = getDeploymentInfoFor(system, env);
			deploymentInfos.put(env, deploymentInfo);
		}

		return deploymentInfos;
	}

	public DeploymentInfo getDeploymentInfoFor(System system, String env)
	{
		return system
				.getEndpoint(env)
				.map((endpoint) -> this.endpointDomainService.getDeploymentInfo(system.getName(), env, endpoint))
				.orElseGet(() -> DeploymentInfo.unvailable(system.getName(), env));
	}
}
