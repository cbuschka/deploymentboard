package com.github.cbuschka.poboard.domain.deployment;

import java.io.IOException;
import java.io.InputStream;

public interface DeploymentInfoExtractionHandler
{
	boolean handles(Endpoint endpoint);

	DeploymentInfo extractDeploymentInfoFrom(Endpoint endpoint, InputStream in, String system, String env) throws IOException;
}
