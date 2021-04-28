package com.github.cbuschka.deploymentboard.domain.issue_tracking;

import com.github.cbuschka.deploymentboard.domain.config.ConfigProvider;
import com.github.cbuschka.deploymentboard.util.Cache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

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
		return this.issueInfoCache.get(issueCode, expiryMillis)
				.orElseGet(() -> getIssueInfoInternal(issueCode));
	}

	private IssueInfo getIssueInfoInternal(String issueCode)
	{
		return configProvider.getConfig().issueTrackers
				.stream()
				.filter((t) -> t.handlesIssueCode(issueCode))
				.map((t) -> getIssueInfoFor(issueCode, t))
				.filter(Optional::isPresent)
				.map(Optional::get)
				.findFirst()
				.orElseGet(() -> unknownIssueInfo(issueCode));
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
				.findFirst().orElseGet(Optional::empty);
	}
}
