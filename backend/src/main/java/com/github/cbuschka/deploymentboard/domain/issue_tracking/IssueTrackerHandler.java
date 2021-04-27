package com.github.cbuschka.deploymentboard.domain.issue_tracking;

import java.util.Optional;

public interface IssueTrackerHandler
{
	boolean handles(IssueTracker issueTracker);

	Optional<IssueInfo> getIssueInfoFor(String issueNo, IssueTracker issueTracker);
}
