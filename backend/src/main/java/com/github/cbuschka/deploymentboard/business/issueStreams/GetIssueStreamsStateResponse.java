package com.github.cbuschka.deploymentboard.business.issueStreams;

import com.github.cbuschka.deploymentboard.domain.issue_tracking.IssueStatus;
import lombok.AllArgsConstructor;

import java.util.List;

@AllArgsConstructor
public class GetIssueStreamsStateResponse
{
	public final List<GetIssueStreamsStateResponse.IssueStream> issueStreams;

	@AllArgsConstructor
	public static class IssueStream
	{
		public String system;
		public String branch;
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
