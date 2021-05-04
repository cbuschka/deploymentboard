package com.github.cbuschka.deploymentboard.domain.issue_tracking;

public class IssueStatus
{
	public final String type;
	public final String label;

	public IssueStatus(String type, String label)
	{
		this.type = type;
		this.label = label;
	}
}
