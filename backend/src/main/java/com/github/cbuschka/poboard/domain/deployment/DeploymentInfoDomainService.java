package com.github.cbuschka.poboard.domain.deployment;


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

	public Map<String, DeploymentInfo> getDeploymentInfosFor(String systemName)
	{
		Map<String, DeploymentInfo> deploymentInfos = new HashMap<>();
		System system = this.systemDomainService.getSystem(systemName);
		for (String env : this.environmentDomainService.getEnvironments())
		{
			Map<String, Endpoint> systemEndpoints = system.getEndpoints();
			Endpoint endpoint = systemEndpoints != null ? systemEndpoints.get(env) : null;
			if (endpoint == null)
			{
				deploymentInfos.put(env, new DeploymentInfo(DeploymentStatus.UNAVAILABLE, systemName, env, null, null, null));
			}
			else
			{
				DeploymentInfo deploymentInfo = this.endpointDomainService.getDeploymentInfo(systemName, env, endpoint);
				deploymentInfos.put(env, deploymentInfo);
			}
		}

		return deploymentInfos;
	}
}
