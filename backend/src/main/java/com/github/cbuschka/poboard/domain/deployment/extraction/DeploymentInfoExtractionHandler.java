package com.github.cbuschka.poboard.domain.deployment.extraction;

import com.github.cbuschka.poboard.domain.deployment.DeploymentInfo;
import com.github.cbuschka.poboard.domain.deployment.Endpoint;

import java.io.IOException;
import java.io.InputStream;

public interface DeploymentInfoExtractionHandler
{
	boolean handles(Endpoint endpoint);

	DeploymentInfo extractDeploymentInfoFrom(Endpoint endpoint, InputStream in, String system, String env) throws IOException;
}
