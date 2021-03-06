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

	public void withSystemEnvironment(String envName, String system, SystemEnvironment systemEnvironment)
	{
		Map<String, SystemEnvironment> env = this.environments.get(envName);
		if (env == null)
		{
			throw new IllegalArgumentException("Unknown env " + envName + ".");
		}
		env.put(system, systemEnvironment);
	}

	public enum LockStatus
	{
		NOT_LOCKABLE, LOCKED, UNLOCKED;
	}

	public static class SystemEnvironment
	{
		public boolean ok;
		public DeploymentStatus status;
		public String version;
		public String commitish;
		public String branch;
		public List<Issue> issues;
		public String message;
		public String buildTimestamp;
		public LockStatus lockStatus;

		public SystemEnvironment(DeploymentStatus status, String version, String commitish, String branch, String buildTimestamp, List<Issue> issues, String message, LockStatus lockStatus)
		{
			this.ok = status == DeploymentStatus.OK;
			this.status = status;
			this.branch = branch;
			this.version = version;
			this.commitish = commitish;
			this.issues = issues;
			this.message = message;
			this.buildTimestamp = buildTimestamp;
			this.lockStatus = lockStatus;
		}
	}

	public static class Issue
	{
		public String issueNo;
		public IssueStatus status;
		public String title;
		public String link;

		public Issue(String issueNo, IssueStatus status, String title, String link)
		{
			this.issueNo = issueNo;
			this.status = status;
			this.title = title;
			this.link = link;
		}
	}
}
