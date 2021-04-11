package com.github.cbuschka.poboard.domain.deployment;


import com.github.cbuschka.poboard.domain.mock.MockDataProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SystemDomainService
{
	@Autowired
	private MockDataProvider mockDataProvider;

	public List<String> getSystems()
	{
		return this.mockDataProvider.getMockData().deploymentInfos
				.stream()
				.map(DeploymentInfo::getSystem)
				.collect(Collectors.toSet())
				.stream()
				.sorted()
				.collect(Collectors.toList());
	}
}
