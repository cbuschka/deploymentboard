package com.github.cbuschka.poboard.business.dashboard;

import com.github.cbuschka.poboard.domain.deployment.DeploymentInfo;
import com.github.cbuschka.poboard.domain.deployment.DeploymentInfoDomainService;
import com.github.cbuschka.poboard.domain.deployment.EnvironmentDomainService;
import com.github.cbuschka.poboard.domain.deployment.System;
import com.github.cbuschka.poboard.domain.deployment.SystemDomainService;
import com.github.cbuschka.poboard.domain.issue_tracking.IssueDomainService;
import com.github.cbuschka.poboard.domain.issue_tracking.IssueStatus;
import com.github.cbuschka.poboard.domain.issue_tracking.Project;
import com.github.cbuschka.poboard.domain.issue_tracking.ProjectDomainService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

@Service
@Transactional
public class GetDashboardStateBusinessService
{
	@Autowired
	private DeploymentInfoDomainService deploymentInfoDomainService;
	@Autowired
	private EnvironmentDomainService environmentDomainService;
	@Autowired
	private GetIssuesByChangeBusinessService getIssuesByChangeBusinessService;
	@Autowired
	private SystemDomainService systemDomainService;
	@Autowired
	private ProjectDomainService projectDomainService;
	@Autowired
	private IssueDomainService issueDomainService;

	public DashboardStateResponse getDashboardState()
	{
		List<String> envs = this.environmentDomainService.getEnvironments();
		List<System> systems = this.systemDomainService.getSystems();
		Set<String> issuePrefixes = projectDomainService.getProjects().stream().map(Project::getIssuePrefix).collect(Collectors.toSet());

		DashboardStateResponse response = DashboardStateResponse.newWithEnvironments(envs.toArray(new String[0]));

		for (System system : systems)
		{
			Map<String, DeploymentInfo> deploymentInfosByEnv = this.deploymentInfoDomainService.getDeploymentInfosFor(system.getName());
			Set<String> issuesOfProd = null;
			String commitishOfProd = null;
			for (String env : envs)
			{
				DeploymentInfo deploymentInfo = deploymentInfosByEnv.get(env);
				Set<String> issues = this.getIssuesByChangeBusinessService.getIssuesFor(issuePrefixes, deploymentInfo.getCommitish(), commitishOfProd, system.getRepository());
				if (issuesOfProd == null)
				{
					response = response.withSystemEnvironment(env, system.getName(),
							new DashboardStateResponse.SystemEnvironment(deploymentInfo.getVersion(), deploymentInfo.getCommitish(),
									deploymentInfo.getBranch(),
									Collections.emptyList()));
					commitishOfProd = deploymentInfo.getCommitish();
					issuesOfProd = issues;
				}
				else
				{
					Set<String> issuesOfEnv = new HashSet<>(issues);
					issuesOfEnv.removeAll(issuesOfProd);

					response = response.withSystemEnvironment(env, system.getName(),
							new DashboardStateResponse.SystemEnvironment(deploymentInfo.getVersion(), deploymentInfo.getCommitish(),
									deploymentInfo.getBranch(),
									issuesOfEnv.stream().map(this::toIssue).collect(toList())
							));
				}
			}
		}

		return response;
	}

	private DashboardStateResponse.Issue toIssue(String issueNo)
	{
		IssueStatus status = this.issueDomainService.getIssueStatus(issueNo);
		return new DashboardStateResponse.Issue(issueNo, status);
	}
}
