package com.github.cbuschka.deploymentboard.domain.issue_tracking;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class IssueInfo
{
	public String issueNo;
	public IssueStatus issueStatus;
	public String title;
	public String url;
}
