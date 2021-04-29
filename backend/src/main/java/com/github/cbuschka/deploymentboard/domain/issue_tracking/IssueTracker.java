package com.github.cbuschka.deploymentboard.domain.issue_tracking;

import java.util.ArrayList;
import java.util.List;

public class IssueTracker
{
	public IssueTrackerType type;
	public String url;
	public String username;
	public List<Project> projects = new ArrayList<>();

	public boolean handlesIssueCode(String issueCode)
	{
		return this.projects
				.stream()
				.map(Project::getIssuePrefix)
				.map(issueCode::startsWith)
				.filter((b) -> b)
				.findFirst()
				.orElse(Boolean.FALSE);
	}

	@Override
	public String toString()
	{
		return "IssueTracker{type=" + type + ",url=" + url + "}";
	}
}
