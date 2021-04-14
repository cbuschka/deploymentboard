package com.github.cbuschka.deploymentboard.domain.deployment.extraction;

import com.github.cbuschka.deploymentboard.domain.deployment.DeploymentInfo;
import com.github.cbuschka.deploymentboard.domain.deployment.DeploymentStatus;
import com.github.cbuschka.deploymentboard.domain.deployment.Endpoint;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.xml.parsers.ParserConfigurationException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class XmlDeploymentInfoExtractionHandlerTest
{
	private static final String system = "system";
	private static final String env = "env";

	@InjectMocks
	private XmlDeploymentInfoExtractionHandler handler;
	@Mock
	private Endpoint endpoint;

	private String xml;
	private DeploymentInfo deploymentInfo;

	@BeforeEach
	void init() throws ParserConfigurationException
	{
		this.handler.init();
	}

	@Test
	void elementsWithNestedTextChecked() throws IOException
	{
		givenXml("<meta><version>VERSION</version><commitish>COMMITISH</commitish><branch>BRANCH</branch></meta>");

		whenExtracted();

		thenAllFound();
	}

	private void thenAllFound()
	{
		assertThat(deploymentInfo.getStatus()).isSameAs(DeploymentStatus.OK);
		assertThat(deploymentInfo.getVersion()).isEqualTo("VERSION");
		assertThat(deploymentInfo.getBranch()).isEqualTo("BRANCH");
		assertThat(deploymentInfo.getCommitish()).isEqualTo("COMMITISH");
	}

	private void whenExtracted() throws IOException
	{
		deploymentInfo = handler.extractDeploymentInfoFrom(endpoint, new ByteArrayInputStream(this.xml.getBytes(StandardCharsets.UTF_8)), system, env);
	}

	private void givenXml(String xml)
	{
		this.xml = xml;
	}

	@Test
	void attributesChecked() throws IOException
	{
		givenXml("<meta version='VERSION' commitish='COMMITISH' branch='BRANCH'/>");

		whenExtracted();

		thenAllFound();
	}

	@Test
	void nestedElementsWithNestedTextChecked() throws IOException
	{
		givenXml("<meta><sub><version>VERSION</version></sub><sub2><sub3><commitish>COMMITISH</commitish><branch>BRANCH</branch></sub3></sub2></meta>");

		whenExtracted();

		thenAllFound();
	}
}
