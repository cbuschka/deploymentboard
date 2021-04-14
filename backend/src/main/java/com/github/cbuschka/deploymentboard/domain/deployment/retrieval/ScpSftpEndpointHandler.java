package com.github.cbuschka.deploymentboard.domain.deployment.retrieval;

import com.github.cbuschka.deploymentboard.domain.auth.AuthDomainService;
import com.github.cbuschka.deploymentboard.domain.auth.PrivateKeyCredentials;
import com.github.cbuschka.deploymentboard.domain.auth.PrivateKeyLoader;
import com.github.cbuschka.deploymentboard.domain.config.Config;
import com.github.cbuschka.deploymentboard.domain.config.ConfigProvider;
import com.github.cbuschka.deploymentboard.domain.deployment.Endpoint;
import com.github.cbuschka.deploymentboard.util.Integers;
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

import java.io.ByteArrayOutputStream;
import java.net.URISyntaxException;
import java.util.List;

@Component
@Slf4j
public class ScpSftpEndpointHandler implements EndpointHandler
{
	@Autowired
	private AuthDomainService authDomainService;
	@Autowired
	private PrivateKeyLoader privateKeyLoader;
	@Autowired
	private ConfigProvider configProvider;

	@Override
	public boolean handles(Endpoint endpoint)
	{
		return endpoint.getUrl().startsWith("scp://") || endpoint.getUrl().startsWith("sftp://");
	}

	@Override
	public byte[] getDeploymentInfo(String system, String env, Endpoint endpoint) throws JSchException, URISyntaxException, SftpException
	{
		Config config = configProvider.getConfig();

		URIish uri = new URIish(endpoint.getUrl());
		JSch jSch = new JSch();
		List<PrivateKeyCredentials> privateKeyCredentialsList = this.authDomainService.getPrivateKeyCredentials(uri.getUser(), uri.getHost());
		for (PrivateKeyCredentials c : privateKeyCredentialsList)
		{
			jSch.addIdentity(uri.getUser(), privateKeyLoader.getAsciiArmoredBytesUTF8(c), null, privateKeyLoader.getPasswordBytesUTF8(c));
		}

		Session session = jSch.getSession(uri.getUser(), uri.getHost(), uri.getPort() != -1 ? uri.getPort() : 22);
		session.setConfig("StrictHostKeyChecking", "no");
		session.setTimeout(Integers.firstNonNull(endpoint.getConnectTimeoutMillis(), config.settings.getConnectTimeoutMillis(), config.defaults.connectTimeoutMillis));
		session.connect();

		Channel channel = session.openChannel("sftp");
		channel.connect();
		ChannelSftp sftpChannel = (ChannelSftp) channel;
		ByteArrayOutputStream bytesOut = new ByteArrayOutputStream();
		sftpChannel.get(uri.getPath(), bytesOut);
		sftpChannel.exit();

		session.disconnect();

		return bytesOut.toByteArray();
	}
}
