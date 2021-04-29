package com.github.cbuschka.deploymentboard.domain.issue_tracking;

import com.github.cbuschka.deploymentboard.domain.config.ConfigProvider;
import com.github.cbuschka.deploymentboard.util.Cache;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class IssueDomainService
{
	@Autowired
	private List<IssueTrackerHandler> issueTrackerHandlers;
	@Autowired
	private ConfigProvider configProvider;

	private final Cache<String, IssueInfo> issueInfoCache = new Cache<>();

	public IssueInfo getIssueInfo(String issueCode)
	{
		int expiryMillis = this.configProvider.getConfig().settings.getRecheckTimeoutMillis();
		return this.issueInfoCache.get(issueCode,
				expiryMillis,
				this::getIssueInfoInternal,
				this::unknownIssueInfo);
	}

	private Optional<IssueInfo> getIssueInfoInternal(String issueCode)
	{
		return configProvider.getConfig().issueTrackers
				.stream()
				.filter((t) -> t.handlesIssueCode(issueCode))
				.map((t) -> getIssueInfoFor(issueCode, t))
				.filter(Optional::isPresent)
				.map(Optional::get)
				.findFirst();
	}

	private IssueInfo unknownIssueInfo(String issueCode)
	{
		return new IssueInfo(issueCode, IssueStatus.UNKNOWN, null, null);
	}

	private Optional<IssueInfo> getIssueInfoFor(String issueCode, IssueTracker issueTracker)
	{
		return this.issueTrackerHandlers
				.stream()
				.filter((h) -> h.handles(issueTracker))
				.map((h) -> h.getIssueInfoFor(issueCode, issueTracker))
				.filter(Optional::isPresent)
				.peek((ii) -> log.info("Got issue info for {} from {}.", issueCode, issueTracker))
				.findFirst().orElseGet(Optional::empty);
	}
}
