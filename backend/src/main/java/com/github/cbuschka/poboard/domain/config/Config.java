package com.github.cbuschka.poboard.domain.config;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.cbuschka.poboard.domain.auth.Credentials;
import com.github.cbuschka.poboard.domain.deployment.Environment;
import com.github.cbuschka.poboard.domain.deployment.System;
import com.github.cbuschka.poboard.domain.issue_tracking.Project;

import java.util.List;

public class Config
{
	@JsonIgnore
	public final Defaults defaults = new Defaults();

	@JsonProperty(value = "defaults")
	public Settings settings = new Settings();

	public String version;

	public Workspace workspace;

	public List<Project> projects;

	public List<Environment> environments;

	public List<System> systems;

	public List<Credentials> credentials;
}
