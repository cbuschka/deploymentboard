package com.github.cbuschka.poboard.business.dashboard;

import com.github.cbuschka.poboard.domain.deployment.DeploymentInfo;
import com.github.cbuschka.poboard.domain.deployment.DeploymentInfoDomainService;
import com.github.cbuschka.poboard.domain.deployment.EnvironmentDomainService;
import com.github.cbuschka.poboard.domain.deployment.SystemDomainService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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

	public DashboardStateResponse getDashboardState()
	{
		List<String> envs = this.environmentDomainService.getEnvironments();
		List<String> systems = this.systemDomainService.getSystems();

		DashboardStateResponse response = DashboardStateResponse.newWithEnvironments(envs.toArray(new String[0]));

		for (String system : systems)
		{
			Map<String, DeploymentInfo> deploymentInfosByEnv = this.deploymentInfoDomainService.getDeploymentInfosFor(system);
			Set<String> issuesOfProd = null;
			for (String env : envs)
			{
				DeploymentInfo deploymentInfo = deploymentInfosByEnv.get(env);
				Set<String> issues = this.getIssuesByChangeBusinessService.getIssuesFor(deploymentInfo.getCommitish());
				if (issuesOfProd == null)
				{
					response = response.withSystemEnvironment(env, system,
							new DashboardStateResponse.SystemEnvironment(deploymentInfo.getVersion(), deploymentInfo.getCommitish(),
									deploymentInfo.getBranch(),
									Collections.emptyList()));
					issuesOfProd = issues;
				}
				else
				{
					Set<String> issuesOfEnv = new HashSet<>(issues);
					issuesOfEnv.removeAll(issuesOfProd);

					response = response.withSystemEnvironment(env, system,
							new DashboardStateResponse.SystemEnvironment(deploymentInfo.getVersion(), deploymentInfo.getCommitish(),
									deploymentInfo.getBranch(),
									issuesOfEnv.stream().map((s) -> new DashboardStateResponse.Issue(s, DashboardStateResponse.IssueStatus.UNKNOWN)).collect(toList())
							));
				}
			}
		}

		return response;
	}
}
