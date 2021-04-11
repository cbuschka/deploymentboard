package com.github.cbuschka.poboard.domain.deployment;


import com.github.cbuschka.poboard.domain.mock.MockDataProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SystemDomainService
{
	@Autowired
	private MockDataProvider mockDataProvider;

	public System getSystem(String system) {
		return getSystems()
				.stream()
				.filter((s)->s.getName().equals(system))
				.findFirst().get();
	}

	public List<System> getSystems()
	{
		return this.mockDataProvider.getMockData().systems;
	}
}
