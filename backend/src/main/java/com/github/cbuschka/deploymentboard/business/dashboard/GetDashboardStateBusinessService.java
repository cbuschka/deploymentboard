package com.github.cbuschka.deploymentboard.business.dashboard;

import com.github.cbuschka.deploymentboard.domain.config.Config;
import com.github.cbuschka.deploymentboard.domain.config.ConfigProvider;
import com.github.cbuschka.deploymentboard.domain.deployment.DeploymentInfo;
import com.github.cbuschka.deploymentboard.domain.deployment.DeploymentInfoDomainService;
import com.github.cbuschka.deploymentboard.domain.deployment.DeploymentStatus;
import com.github.cbuschka.deploymentboard.domain.deployment.Environment;
import com.github.cbuschka.deploymentboard.domain.deployment.EnvironmentDomainService;
import com.github.cbuschka.deploymentboard.domain.deployment.System;
import com.github.cbuschka.deploymentboard.domain.deployment.SystemDomainService;
import com.github.cbuschka.deploymentboard.domain.issue_tracking.IssueDomainService;
import com.github.cbuschka.deploymentboard.domain.issue_tracking.IssueInfo;
import com.github.cbuschka.deploymentboard.domain.issue_tracking.ProjectDomainService;
import com.github.cbuschka.deploymentboard.domain.scm.CodeRepository;
import com.github.cbuschka.deploymentboard.util.CachedValueHolder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executors;

import static java.util.stream.Collectors.toList;

@Service
@Slf4j
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
	@Autowired
	private ConfigProvider configProvider;

	private final CachedValueHolder<DashboardStateResponse> stateResponseCachedValueHolder = new CachedValueHolder<>(this::calculateDashboardState, Executors.newSingleThreadExecutor());

	@PostConstruct
	private void init()
	{
		this.stateResponseCachedValueHolder.setDefault(getEmptyState());
	}

	@Scheduled(fixedDelay = 1_000)
	public DashboardStateResponse getDashboardState()
	{
		Config config = this.configProvider.getConfig();
		return this.stateResponseCachedValueHolder.get(config.settings.getConnectTimeoutMillis());
	}

	private DashboardStateResponse getEmptyState()
	{
		List<Environment> envs = this.environmentDomainService.getEnvironments();
		List<System> systems = this.systemDomainService.getSystems();

		DashboardStateResponse response = DashboardStateResponse.newWithEnvironments(envs.stream().map(Environment::getName).toArray(String[]::new));
		for (System system : systems)
		{
			for (Environment env : envs)
			{
				response.withSystemEnvironment(env.getName(), system.getName(),
						new DashboardStateResponse.SystemEnvironment(DeploymentStatus.UNKNOWN, null, null, null, null, Collections.emptyList(), null, DashboardStateResponse.LockStatus.NOT_LOCKABLE));
			}
		}

		return response;
	}

	private DashboardStateResponse calculateDashboardState()
	{
		long startMillis = java.lang.System.currentTimeMillis();

		List<Environment> envs = this.environmentDomainService.getEnvironments();
		List<System> systems = this.systemDomainService.getSystems();
		Set<String> issuePrefixes = projectDomainService.getAllIssuePrefixes();

		DashboardStateResponse response = DashboardStateResponse.newWithEnvironments(envs.stream().map(Environment::getName).toArray(String[]::new));

		for (System system : systems)
		{
			Map<String, DeploymentInfo> deploymentInfosByEnv = this.deploymentInfoDomainService.getDeploymentInfosFor(system);
			String prodCommitish = null;
			CodeRepository codeRepository = system.getRepository();
			for (Environment env : envs)
			{
				DeploymentInfo deploymentInfo = deploymentInfosByEnv.get(env.getName());
				if (prodCommitish == null || codeRepository == null)
				{
					response.withSystemEnvironment(env.getName(), system.getName(),
							new DashboardStateResponse.SystemEnvironment(deploymentInfo.getStatus(), deploymentInfo.getVersion(), deploymentInfo.getCommitish(),
									deploymentInfo.getBranch(), deploymentInfo.getBuildTimestamp(), Collections.emptyList(), deploymentInfo.getMessage(), prodCommitish == null ? DashboardStateResponse.LockStatus.NOT_LOCKABLE : DashboardStateResponse.LockStatus.UNLOCKED));
					prodCommitish = deploymentInfo.getCommitish();
				}
				else
				{
					Set<String> issues = this.getIssuesByChangeBusinessService.getIssuesFor(issuePrefixes, deploymentInfo.getCommitish(), prodCommitish, codeRepository);
					response.withSystemEnvironment(env.getName(), system.getName(),
							new DashboardStateResponse.SystemEnvironment(deploymentInfo.getStatus(), deploymentInfo.getVersion(), deploymentInfo.getCommitish(),
									deploymentInfo.getBranch(), deploymentInfo.getBuildTimestamp(), issues.stream().map(this::toIssue).collect(toList()),
									deploymentInfo.getMessage(), DashboardStateResponse.LockStatus.UNLOCKED));
				}
			}
		}

		long durationMillis = java.lang.System.currentTimeMillis() - startMillis;
		if (durationMillis > 10_000)
		{
			log.warn("Loading state slow: {} milli(s).", durationMillis);
		}

		return response;
	}

	private DashboardStateResponse.Issue toIssue(String issueNo)
	{
		IssueInfo info = this.issueDomainService.getIssueInfo(issueNo);
		return new DashboardStateResponse.Issue(issueNo, info.issueStatus, info.title, info.url);
	}
}
