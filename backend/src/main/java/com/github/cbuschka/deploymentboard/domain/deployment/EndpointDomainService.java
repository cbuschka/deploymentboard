package com.github.cbuschka.deploymentboard.domain.deployment;

import com.github.cbuschka.deploymentboard.domain.deployment.extraction.DeploymentInfoExtractor;
import com.github.cbuschka.deploymentboard.domain.deployment.retrieval.DeploymentInfoRetriever;
import com.github.cbuschka.deploymentboard.util.Cache;
import com.github.cbuschka.deploymentboard.util.CacheStats;
import com.github.cbuschka.deploymentboard.util.CacheStatsProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.util.Optional;

@Service
public class EndpointDomainService implements CacheStatsProvider
{
	@Autowired
	private DeploymentInfoRetriever retriever;
	@Autowired
	private DeploymentInfoExtractor extractor;

	private final Cache<SystemEnvironment, DeploymentInfo> deploymentInfoCache = new Cache<>();

	@Override
	public CacheStats getCacheStats() {
		return new CacheStats("deploymentInfoCache", deploymentInfoCache.getEntryCount());
	}

	public DeploymentInfo getDeploymentInfo(String system, String env, Endpoint endpoint)
	{
		int expiryMillis = endpoint.getRecheckTimeoutMillis();
		return this.deploymentInfoCache.get(new SystemEnvironment(system, env),
				expiryMillis,
				(systemEnvironment) -> this.loadDeploymentInfo(systemEnvironment, endpoint),
				(systemEnvironment) -> DeploymentInfo.failure(systemEnvironment.getSystem(), systemEnvironment.getEnvironment(), "Not reachable."));
	}

	private Optional<DeploymentInfo> loadDeploymentInfo(SystemEnvironment systemEnvironment, Endpoint endpoint)
	{
		String system = systemEnvironment.getSystem();
		String env = systemEnvironment.getEnvironment();
		try
		{
			byte[] result = this.retriever.retrieveDeploymentInfoFrom(system, env, endpoint);
			return Optional.of(this.extractor.extractDeploymentInfoFrom(new ByteArrayInputStream(result), system, env, endpoint));
		}
		catch (Exception ex)
		{
			return Optional.of(DeploymentInfo.failure(system, env, ex.getMessage()));
		}
	}
}
