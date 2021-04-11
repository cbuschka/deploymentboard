package com.github.cbuschka.poboard.domain.deployment;


import com.github.cbuschka.poboard.domain.mock.MockDataProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class DeploymentInfoDomainService
{
	@Autowired
	private MockDataProvider mockDataProvider;

	public Map<String, DeploymentInfo> getDeploymentInfosFor(String system)
	{
		return mockDataProvider.getMockData().deploymentInfos
				.stream()
				.filter((d) -> Objects.equals(d.getSystem(), system))
				.collect(Collectors.toMap(DeploymentInfo::getEnv, p -> p, (p, q) -> p));
	}
}
