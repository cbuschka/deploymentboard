package com.github.cbuschka.deploymentboard.domain.issue_tracking;

import com.github.cbuschka.deploymentboard.domain.config.Config;
import com.github.cbuschka.deploymentboard.domain.config.ConfigProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class ProjectDomainService
{
	@Autowired
	private ConfigProvider configProvider;

	public Set<String> getAllIssuePrefixes()
	{
		Config config = this.configProvider.getConfig();
		return Stream.concat(config
						.issueTrackers
						.stream()
						.flatMap((t) -> t.projects.stream().map(Project::getIssuePrefix)),
				this.configProvider.getConfig()
						.projects
						.stream().map(Project::getIssuePrefix))
				.collect(Collectors.toSet());
	}
}
