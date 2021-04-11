package com.github.cbuschka.poboard.domain.mock;

import com.github.cbuschka.poboard.domain.auth.Credentials;
import com.github.cbuschka.poboard.domain.deployment.System;
import com.github.cbuschka.poboard.domain.issue_tracking.Project;

import java.util.List;

public class MockData
{
	public List<Project> projects;

	public List<String> environments;

	public List<System> systems;

	public List<Credentials> credentials;
}
