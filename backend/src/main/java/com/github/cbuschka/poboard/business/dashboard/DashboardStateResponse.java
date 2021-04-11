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

	public DashboardStateResponse withSystemEnvironment(String envName, String system, SystemEnvironment systemEnvironment)
	{
		Map<String, SystemEnvironment> env = this.environments.get(envName);
		if( env == null ) {
			throw new IllegalArgumentException("Unknown env "+envName+".");
		}
		env.put(system, systemEnvironment);
		return this;
	}

	public static class SystemEnvironment
	{
		public String version;
		public String commitish;
		public String branch;
		public List<Issue> issues;

		public SystemEnvironment(String version, String commitish, String branch, List<Issue> issues)
		{
			this.branch = branch;
			this.version = version;
			this.commitish = commitish;
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
		OPEN, CLOSED, MISSING, UNKNOWN;
	}
}
