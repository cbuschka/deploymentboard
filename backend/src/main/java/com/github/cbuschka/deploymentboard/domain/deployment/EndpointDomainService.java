package com.github.cbuschka.deploymentboard.domain.deployment;

import com.github.cbuschka.deploymentboard.domain.deployment.extraction.DeploymentInfoExtractor;
import com.github.cbuschka.deploymentboard.domain.deployment.retrieval.DeploymentInfoRetriever;
import com.github.cbuschka.deploymentboard.util.Cache;
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

	private final Cache<String, DeploymentInfo> deploymentInfoCache = new Cache<>();

	public DeploymentInfo getDeploymentInfo(String system, String env, Endpoint endpoint)
	{
		String key = system + "/" + env;
		int expiryMillis = endpoint.getRecheckTimeoutMillis();
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
