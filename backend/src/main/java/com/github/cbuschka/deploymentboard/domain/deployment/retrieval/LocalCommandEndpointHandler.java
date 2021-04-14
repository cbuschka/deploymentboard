package com.github.cbuschka.deploymentboard.domain.deployment.retrieval;

import com.github.cbuschka.deploymentboard.domain.deployment.Endpoint;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class LocalCommandEndpointHandler implements EndpointHandler
{
	@Override
	public boolean handles(Endpoint endpoint)
	{
		return endpoint.getUrl() == null && endpoint.getCommand() != null;
	}

	@Override
	public byte[] getDeploymentInfo(String system, String env, Endpoint endpoint) throws IOException, InterruptedException
	{
		Process process = new ProcessBuilder().command("bash", "-c", endpoint.getCommand())
				.redirectErrorStream(true)
				.redirectOutput(ProcessBuilder.Redirect.PIPE)
				.start();
		byte[] output = process.getInputStream().readAllBytes();
		int exitCode = process.waitFor();
		if (exitCode != 0)
		{
			throw new IOException("Exit code: " + exitCode);
		}

		return output;
	}
}
