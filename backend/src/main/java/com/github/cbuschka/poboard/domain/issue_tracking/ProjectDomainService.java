package com.github.cbuschka.poboard.domain.issue_tracking;

import com.github.cbuschka.poboard.domain.config.ConfigProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class ProjectDomainService
{
	@Autowired
	private ConfigProvider configProvider;

	Optional<Project> getProjectFor(String issueNo)
	{
		return getProjects()
				.stream()
				.filter((p) -> issueNo.startsWith(p.getIssuePrefix()))
				.findFirst();
	}

	public List<Project> getProjects()
	{
		return Optional.ofNullable(configProvider.getConfig().projects)
				.orElseGet(Collections::emptyList);
	}
}
