package com.github.cbuschka.poboard.domain.deployment;

import com.github.cbuschka.poboard.domain.config.Config;
import com.github.cbuschka.poboard.domain.config.ConfigProvider;
import com.github.cbuschka.poboard.util.Cache;
import com.github.cbuschka.poboard.util.Integers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EndpointDomainService
{
	@Autowired
	private List<EndpointHandler> endpointHandlers;
	@Autowired
	private ConfigProvider configProvider;

	private final Cache<String, DeploymentInfo> deploymentInfoCache = new Cache<>();

	public DeploymentInfo getDeploymentInfo(String system, String env, Endpoint endpoint)
	{
		Config config = configProvider.getConfig();

		String key = system + "/" + env;
		int expiryMillis = Integers.firstNonNull(endpoint.getRecheckTimeoutMillis(),
				config.settings.getRecheckTimeoutMillis(),
				config.defaults.recheckTimeoutMillis);
		return this.deploymentInfoCache.get(key, expiryMillis)
				.orElseGet(() -> loadDeploymentInfo(system, env, endpoint));
	}

	private DeploymentInfo loadDeploymentInfo(String system, String env, Endpoint endpoint)
	{
		for (EndpointHandler handler : this.endpointHandlers)
		{
			if (handler.handles(endpoint))
			{
				return handler.getDeploymentInfo(system, env, endpoint);
			}
		}

		return DeploymentInfo.failure(system, env, "Unknown protocol.");
	}
}
