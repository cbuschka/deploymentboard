package com.github.cbuschka.deploymentboard.domain.deployment.retrieval;

import com.github.cbuschka.deploymentboard.domain.deployment.Endpoint;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ScpSftpEndpointHandlerTest
{
	@InjectMocks
	private ScpSftpEndpointHandler handler;

	@Mock
	private Endpoint endpoint;

	private boolean handles;

	@Test
	void deniesHandlingNullUrl()
	{
		givenEndpointWithoutUrl();

		whenAskedIfEndpointHandled();

		thenHandlingIsDenied();
	}

	private void thenHandlingIsDenied()
	{
		assertThat(this.handles).isFalse();
	}

	private void whenAskedIfEndpointHandled()
	{
		this.handles = handler.handles(endpoint);
	}

	private void givenEndpointWithoutUrl()
	{
		when(this.endpoint.getUrl()).thenReturn(null);
	}

}
