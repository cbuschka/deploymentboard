package com.github.cbuschka.deploymentboard.business.issues;

import com.github.cbuschka.deploymentboard.business.dashboard.GetIssuesByChangeBusinessService;
import com.github.cbuschka.deploymentboard.domain.config.Config;
import com.github.cbuschka.deploymentboard.domain.config.ConfigProvider;
import com.github.cbuschka.deploymentboard.domain.deployment.DeploymentInfo;
import com.github.cbuschka.deploymentboard.domain.deployment.DeploymentInfoDomainService;
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
import java.util.stream.Collectors;

@Slf4j
@Service
public class GetIssuesStateBusinessService
{
	@Autowired
	private SystemDomainService systemDomainService;
	@Autowired
	private EnvironmentDomainService environmentDomainService;
	@Autowired
	private IssueDomainService issueDomainService;
	@Autowired
	private GetIssuesByChangeBusinessService getIssuesByChangeBusinessService;
	@Autowired
	private ProjectDomainService projectDomainService;
	@Autowired
	private DeploymentInfoDomainService deploymentInfoDomainService;
	@Autowired
	private ConfigProvider configProvider;

	private final CachedValueHolder<GetIssuesStateResponse> issuesStateResponseCachedValueHolder = new CachedValueHolder<>(this::calculateIssuesState, Executors.newSingleThreadExecutor());

	@PostConstruct
	private void init()
	{
		this.issuesStateResponseCachedValueHolder.setDefault(getEmptyIssuesState());
	}

	private GetIssuesStateResponse getEmptyIssuesState()
	{
		return new GetIssuesStateResponse(Collections.emptyList());
	}

	@Scheduled(fixedDelay = 1_000)
	public void reloadIssuesState()
	{
		getIssuesState();
	}

	public GetIssuesStateResponse getIssuesState()
	{
		Config config = this.configProvider.getConfig();
		return this.issuesStateResponseCachedValueHolder.get(config.settings.getRecheckTimeoutMillis());
	}

	private GetIssuesStateResponse calculateIssuesState()
	{
		List<GetIssuesStateResponse.System> systems = this.systemDomainService.getSystems()
				.stream()
				.map(this::getSystemWithIssuesFor).collect(Collectors.toList());
		return new GetIssuesStateResponse(systems);
	}

	private GetIssuesStateResponse.System getSystemWithIssuesFor(System system)
	{
		return new GetIssuesStateResponse.System(system.getName(), getIssuesFor(system));
	}

	private List<GetIssuesStateResponse.Issue> getIssuesFor(System system)
	{
		List<Environment> envs = this.environmentDomainService.getEnvironments();
		Set<String> issuePrefixes = projectDomainService.getAllIssuePrefixes();

		Map<String, DeploymentInfo> deploymentInfosByEnv = this.deploymentInfoDomainService.getDeploymentInfosFor(system);
		String prodCommitish = null;
		CodeRepository codeRepository = system.getRepository();
		for (Environment env : envs)
		{
			DeploymentInfo deploymentInfo = deploymentInfosByEnv.get(env.getName());
			if (prodCommitish == null || codeRepository == null)
			{
				prodCommitish = deploymentInfo.getCommitish();
			}
		}

		Set<String> issueNos = getIssuesByChangeBusinessService.getIssuesFor(issuePrefixes, system.getMainBranch(), prodCommitish, codeRepository);
		return issueNos.stream().map(this::toIssue).collect(Collectors.toList());
	}

	private GetIssuesStateResponse.Issue toIssue(String issueNo)
	{
		IssueInfo info = this.issueDomainService.getIssueInfo(issueNo);
		return new GetIssuesStateResponse.Issue(issueNo, info.issueStatus, info.title, info.url);
	}
}
