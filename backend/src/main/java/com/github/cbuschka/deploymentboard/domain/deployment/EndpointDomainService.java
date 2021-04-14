package com.github.cbuschka.deploymentboard.domain.deployment;

import com.github.cbuschka.deploymentboard.domain.config.Config;
import com.github.cbuschka.deploymentboard.domain.config.ConfigProvider;
import com.github.cbuschka.deploymentboard.domain.deployment.extraction.DeploymentInfoExtractor;
import com.github.cbuschka.deploymentboard.domain.deployment.retrieval.DeploymentInfoRetriever;
import com.github.cbuschka.deploymentboard.util.Cache;
import com.github.cbuschka.deploymentboard.util.Integers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;

@Service
public class EndpointDomainService
{
	@Autowired
	private DeploymentInfoRetriever retriever;
	@Autowired
	private DeploymentInfoExtractor extractor;
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
		try
		{
			byte[] result = this.retriever.extractDeploymentInfoFrom(system, env, endpoint);
			return this.extractor.extractDeploymentInfoFrom(new ByteArrayInputStream(result), system, env, endpoint);
		}
		catch (Exception ex)
		{
			return DeploymentInfo.failure(system, env, ex.getMessage());
		}
	}
}
