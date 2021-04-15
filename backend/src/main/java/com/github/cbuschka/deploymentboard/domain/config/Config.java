package com.github.cbuschka.deploymentboard.domain.config;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.cbuschka.deploymentboard.domain.auth.Credentials;
import com.github.cbuschka.deploymentboard.domain.deployment.Environment;
import com.github.cbuschka.deploymentboard.domain.deployment.System;
import com.github.cbuschka.deploymentboard.domain.issue_tracking.Project;

import java.util.ArrayList;
import java.util.List;

public class Config
{
	@JsonProperty(value = "defaults")
	public Settings settings = new Settings();

	public String version;

	public Workspace workspace;

	public List<Project> projects = new ArrayList<>();

	public List<Environment> environments = new ArrayList<>();

	public List<System> systems = new ArrayList<>();

	public List<Credentials> credentials = new ArrayList<>();

	public AuthMethod auth = new SimpleAuthMethod();
}
