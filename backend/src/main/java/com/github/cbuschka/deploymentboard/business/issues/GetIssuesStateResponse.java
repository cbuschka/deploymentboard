package com.github.cbuschka.deploymentboard.business.issues;

import com.github.cbuschka.deploymentboard.domain.issue_tracking.IssueStatus;
import lombok.AllArgsConstructor;

import java.util.List;

@AllArgsConstructor
public class GetIssuesStateResponse
{
	public final List<GetIssuesStateResponse.System> systems;

	@AllArgsConstructor
	public static class System
	{
		public String name;
		public List<Issue> issues;
	}

	@AllArgsConstructor
	public static class Issue
	{
		public final String issueNo;
		public final IssueStatus status;
		public final String title;
		public final String link;
	}
}
