package com.github.cbuschka.deploymentboard.domain.issue_tracking;

import java.util.Arrays;
import java.util.List;

public class JiraIssueStatusMapper
{
	private static final List<String> OPEN_NAMES = Arrays.asList("OPEN", "READY", "STARTED", "IN PROGRESS", "IN REVIEW", "TESTING", "IN TEST", "IN WORK");
	private static final List<String> CLOSED_NAMES = Arrays.asList("FINISHED", "DONE", "CLOSED", "IN RELEASE", "REJECTED", "ABORTED", "REVOKED", "RESOLVED", "SOLVED");

	public IssueStatus map(JiraGetIssueResponse.Status status)
	{
		if (status == null || status.name == null)
		{
			return new IssueStatus("UNKNOWN", "Unknown");
		}

		for (String name : OPEN_NAMES)
		{
			if (name.equalsIgnoreCase(status.name))
			{
				return new IssueStatus("OPEN", status.name);
			}
		}

		for (String name : CLOSED_NAMES)
		{
			if (name.equalsIgnoreCase(status.name))
			{
				return new IssueStatus("CLOSED", status.name);
			}
		}

		return new IssueStatus("UNKNOWN", status.name);
	}
}
