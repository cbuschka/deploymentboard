package com.github.cbuschka.deploymentboard.domain.issue_tracking;

import com.github.cbuschka.deploymentboard.domain.config.ConfigProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ProjectDomainService
{
	@Autowired
	private ConfigProvider configProvider;

	public Set<String> getAllIssuePrefixes()
	{
		return this.configProvider.getConfig()
				.issueTrackers
				.stream()
				.flatMap((t) -> t.projects.stream().map(Project::getIssuePrefix))
				.collect(Collectors.toSet());
	}
}
