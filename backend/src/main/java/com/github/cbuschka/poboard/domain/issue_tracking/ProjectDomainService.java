package com.github.cbuschka.poboard.domain.issue_tracking;

import com.github.cbuschka.poboard.domain.mock.MockDataProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProjectDomainService
{
	@Autowired
	private MockDataProvider mockDataProvider;

	public List<Project> getProjects()
	{
		return mockDataProvider.getMockData().projects;
	}
}
