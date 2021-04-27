package com.github.cbuschka.deploymentboard.domain.issue_tracking;

public class JiraGetIssueResponse
{
	public String key;
	public Fields fields;

	public static class Fields
	{
		public String summary;
		public Status status;
	}

	public static class Status
	{
		public String name;
	}
}
