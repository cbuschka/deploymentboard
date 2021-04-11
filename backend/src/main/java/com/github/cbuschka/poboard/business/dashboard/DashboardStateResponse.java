package com.github.cbuschka.poboard.business.dashboard;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class DashboardStateResponse
{
	public final Map<String, Map<String, SystemEnvironment>> environments = new LinkedHashMap<>();

	public static DashboardStateResponse newWithEnvironments(String... envs)
	{
		DashboardStateResponse response = new DashboardStateResponse();
		for (String env : envs)
		{
			response.environments.put(env, new LinkedHashMap<>());
		}
		return response;
	}

	public DashboardStateResponse withSystemEnvironment(String env, String system, SystemEnvironment systemEnvironment)
	{
		this.environments.get(env).put(system, systemEnvironment);
		return this;
	}

	public static class SystemEnvironment
	{
		public String version;
		public List<Issue> issues;

		public SystemEnvironment(String version, List<Issue> issues)
		{
			this.version = version;
			this.issues = issues;
		}
	}

	public static class Issue {
		public String issueNo;
		public IssueStatus status;

		public Issue(String issueNo, IssueStatus status)
		{
			this.issueNo = issueNo;
			this.status = status;
		}
	}

	public enum IssueStatus
	{
		OPEN, CLOSED, MISSING;
	}
}
