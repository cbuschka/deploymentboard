package com.github.cbuschka.poboard.domain.issue_tracking;

import com.github.cbuschka.poboard.domain.mock.MockDataProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProjectDomainService
{
	@Autowired
	private MockDataProvider mockDataProvider;

	Optional<Project> getProjectFor(String issueNo)
	{
		return getProjects()
				.stream()
				.filter((p) -> issueNo.startsWith(p.getIssuePrefix()))
				.findFirst();
	}

	public List<Project> getProjects()
	{
		return mockDataProvider.getMockData().projects;
	}
}
