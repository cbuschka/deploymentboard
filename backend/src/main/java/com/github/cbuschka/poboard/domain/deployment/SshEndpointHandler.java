package com.github.cbuschka.poboard.domain.deployment;

import com.github.cbuschka.poboard.domain.auth.AuthDomainService;
import com.github.cbuschka.poboard.domain.auth.PrivateKeyCredentials;
import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.jgit.transport.URIish;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Component
@Slf4j
public class SshEndpointHandler implements EndpointHandler
{
	@Autowired
	private AuthDomainService authDomainService;
	@Autowired
	private DeploymentInfoExtractor deploymentInfoExtractor;

	@Override
	public boolean handles(Endpoint endpoint)
	{
		return endpoint.getUrl().startsWith("ssh://") || endpoint.getUrl().startsWith("sftp://");
	}

	@Override
	public DeploymentInfo getDeploymentInfo(String system, String env, Endpoint endpoint)
	{

		try
		{
			URIish uri = new URIish(endpoint.getUrl());
			JSch jSch = new JSch();
			List<PrivateKeyCredentials> privateKeyCredentialsList = this.authDomainService.getPrivateKeyCredentials(uri.getUser(), uri.getHost());
			for (PrivateKeyCredentials c : privateKeyCredentialsList)
			{
				jSch.addIdentity(c.getUsername(), c.getData().getBytes(StandardCharsets.UTF_8), null, null);
			}

			Session session = jSch.getSession(uri.getUser(), uri.getHost(), uri.getPort() != -1 ? uri.getPort() : 22);
			session.setConfig("StrictHostKeyChecking", "no");
			session.connect();

			Channel channel = session.openChannel("sftp");
			channel.connect();
			ChannelSftp sftpChannel = (ChannelSftp) channel;
			ByteArrayOutputStream bytesOut = new ByteArrayOutputStream();
			sftpChannel.get(uri.getPath(), bytesOut);
			sftpChannel.exit();

			session.disconnect();

			return this.deploymentInfoExtractor.extractDeploymentInfoFrom(new ByteArrayInputStream(bytesOut.toByteArray()), system, env, endpoint);
		}
		catch (IOException | URISyntaxException | JSchException | SftpException ex)
		{
			log.error("Getting deployment info for {} failed.", endpoint.getUrl(), ex);

			return DeploymentInfo.unvailable(system, env);
		}

	}
}
