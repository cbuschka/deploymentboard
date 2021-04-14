package com.github.cbuschka.deploymentboard.business.dashboard;

import com.github.cbuschka.deploymentboard.domain.deployment.DeploymentStatus;
import com.github.cbuschka.deploymentboard.domain.issue_tracking.IssueStatus;

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
		if (env == null)
		{
			throw new IllegalArgumentException("Unknown env " + envName + ".");
		}
		env.put(system, systemEnvironment);
		return this;
	}

	public static class SystemEnvironment
	{
		public boolean ok;
		public String version;
		public String commitish;
		public String branch;
		public List<Issue> issues;
		public String message;
		public String buildTimestamp;

		public SystemEnvironment(DeploymentStatus status, String version, String commitish, String branch, String buildTimestamp, List<Issue> issues, String message)
		{
			this.ok = status == DeploymentStatus.AVAILABLE;
			this.branch = branch;
			this.version = version;
			this.commitish = commitish;
			this.issues = issues;
			this.message = message;
			this.buildTimestamp = buildTimestamp;
		}
	}

	public static class Issue
	{
		public String issueNo;
		public IssueStatus status;

		public Issue(String issueNo, IssueStatus status)
		{
			this.issueNo = issueNo;
			this.status = status;
		}
	}
}
