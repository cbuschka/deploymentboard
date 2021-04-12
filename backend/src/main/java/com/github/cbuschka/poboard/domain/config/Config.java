package com.github.cbuschka.poboard.domain.config;

import com.github.cbuschka.poboard.domain.auth.Credentials;
import com.github.cbuschka.poboard.domain.deployment.Environment;
import com.github.cbuschka.poboard.domain.deployment.System;
import com.github.cbuschka.poboard.domain.issue_tracking.Project;

import java.util.List;

public class Config
{
	public List<Project> projects;

	public List<Environment> environments;

	public List<System> systems;

	public List<Credentials> credentials;
}
