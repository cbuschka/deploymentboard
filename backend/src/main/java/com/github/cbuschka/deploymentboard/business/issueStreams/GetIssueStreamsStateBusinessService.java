package com.github.cbuschka.deploymentboard.business.issueStreams;

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
public class GetIssueStreamsStateBusinessService
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

	private final CachedValueHolder<GetIssueStreamsStateResponse> issuesStateResponseCachedValueHolder = new CachedValueHolder<>(this::calculateIssuesState, Executors.newSingleThreadExecutor());

	@PostConstruct
	private void init()
	{
		this.issuesStateResponseCachedValueHolder.setDefault(getEmptyIssuesState());
	}

	private GetIssueStreamsStateResponse getEmptyIssuesState()
	{
		return new GetIssueStreamsStateResponse(Collections.emptyList());
	}

	@Scheduled(fixedDelay = 1_000)
	public void reloadIssuesState()
	{
		getIssuesState();
	}

	public GetIssueStreamsStateResponse getIssuesState()
	{
		Config config = this.configProvider.getConfig();
		return this.issuesStateResponseCachedValueHolder.get(config.settings.getRecheckTimeoutMillis());
	}

	private GetIssueStreamsStateResponse calculateIssuesState()
	{
		List<GetIssueStreamsStateResponse.IssueStream> issueStreams = this.systemDomainService.getSystems()
				.stream()
				.map(this::getIssueStreamFor).collect(Collectors.toList());
		return new GetIssueStreamsStateResponse(issueStreams);
	}

	private GetIssueStreamsStateResponse.IssueStream getIssueStreamFor(System system)
	{
		return new GetIssueStreamsStateResponse.IssueStream(system.getName(), system.getMainBranch(), getIssuesFor(system));
	}

	private List<GetIssueStreamsStateResponse.Issue> getIssuesFor(System system)
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

	private GetIssueStreamsStateResponse.Issue toIssue(String issueNo)
	{
		IssueInfo info = this.issueDomainService.getIssueInfo(issueNo);
		return new GetIssueStreamsStateResponse.Issue(issueNo, info.issueStatus, info.title, info.url);
	}
}
