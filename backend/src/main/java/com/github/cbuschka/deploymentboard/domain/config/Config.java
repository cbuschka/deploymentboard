package com.github.cbuschka.deploymentboard.domain.config;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.cbuschka.deploymentboard.domain.auth.Credentials;
import com.github.cbuschka.deploymentboard.domain.deployment.Environment;
import com.github.cbuschka.deploymentboard.domain.deployment.System;
import com.github.cbuschka.deploymentboard.domain.issue_tracking.IssueTracker;
import com.github.cbuschka.deploymentboard.domain.issue_tracking.Project;

import java.util.ArrayList;
import java.util.List;

public class Config
{
	@JsonProperty(value = "defaults")
	public Settings settings = new Settings();

	public String version;

	public Workspace workspace;

	public List<Project> projects;

	public List<Environment> environments;

	public List<System> systems;

	public List<Credentials> credentials;

	public List<IssueTracker> issueTrackers;
}
