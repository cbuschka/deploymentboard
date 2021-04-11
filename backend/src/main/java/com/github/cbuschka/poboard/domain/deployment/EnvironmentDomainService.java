package com.github.cbuschka.poboard.domain.deployment;

import com.github.cbuschka.poboard.domain.mock.MockDataProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EnvironmentDomainService
{
	@Autowired
	private MockDataProvider mockDataProvider;

	public List<String> getEnvironments()
	{
		return mockDataProvider.getMockData().environments;
	}
}
