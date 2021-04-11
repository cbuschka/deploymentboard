package com.github.cbuschka.poboard.domain.mock;

import com.github.cbuschka.poboard.domain.deployment.DeploymentInfo;
import com.github.cbuschka.poboard.domain.deployment.System;
import com.github.cbuschka.poboard.domain.issue_tracking.Project;
import com.github.cbuschka.poboard.domain.scm.Change;

import java.util.List;

public class MockData
{
	public List<Project> projects;

	public List<String> environments;

	public List<DeploymentInfo> deploymentInfos;

	public List<System> systems;
}
