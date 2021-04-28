package com.github.cbuschka.deploymentboard.domain.issue_tracking;

import lombok.AllArgsConstructor;
import lombok.ToString;

@AllArgsConstructor
@ToString
public class IssueInfo
{
	public String issueNo;
	public IssueStatus issueStatus;
	public String title;
	public String url;
}
