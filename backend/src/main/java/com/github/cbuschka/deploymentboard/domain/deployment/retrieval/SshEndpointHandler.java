package com.github.cbuschka.deploymentboard.domain.deployment.retrieval;

import com.github.cbuschka.deploymentboard.domain.auth.AuthDomainService;
import com.github.cbuschka.deploymentboard.domain.auth.PrivateKeyCredentials;
import com.github.cbuschka.deploymentboard.domain.auth.PrivateKeyLoader;
import com.github.cbuschka.deploymentboard.domain.deployment.Endpoint;
import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.jgit.transport.URIish;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
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
	private PrivateKeyLoader privateKeyLoader;

	@Override
	public boolean handles(Endpoint endpoint)
	{
		return endpoint.getUrl() != null && endpoint.getUrl().startsWith("ssh://");
	}

	@Override
	public byte[] getDeploymentInfo(String system, String env, Endpoint endpoint) throws URISyntaxException, JSchException, IOException
	{
		if (endpoint.getCommand() == null)
		{
			throw new IllegalStateException("No command set.");
		}

		if (endpoint.getFormat() == null)
		{
			throw new IllegalStateException("No format configured.");
		}

		URIish uri = new URIish(endpoint.getUrl());
		JSch jSch = new JSch();
		List<PrivateKeyCredentials> privateKeyCredentialsList = this.authDomainService.getPrivateKeyCredentials(uri.getUser(), uri.getHost());
		for (PrivateKeyCredentials c : privateKeyCredentialsList)
		{
			jSch.addIdentity(uri.getUser(), privateKeyLoader.getAsciiArmoredBytesUTF8(c), null, privateKeyLoader.getPasswordBytesUTF8(c));
		}

		Session session = jSch.getSession(uri.getUser(), uri.getHost(), uri.getPort() != -1 ? uri.getPort() : 22);
		try
		{
			session.setTimeout(endpoint.getConnectTimeoutMillis());
			session.setConfig("StrictHostKeyChecking", "no");
			session.connect();

			Channel channel = session.openChannel("exec");
			ChannelExec execChannel = (ChannelExec) channel;
			execChannel.setCommand(endpoint.getCommand());
			execChannel.setInputStream(null);
			ByteArrayOutputStream stderrOut = new ByteArrayOutputStream();
			execChannel.setErrStream(stderrOut);
			ByteArrayOutputStream stdoutOut = new ByteArrayOutputStream();
			execChannel.connect();
			execChannel.start();

			readResponseInto(channel, execChannel, stdoutOut);

			int exitStatus = execChannel.getExitStatus();
			if (exitStatus != 0)
			{
				throw new IOException("Command exit code: " + exitStatus + "; " + stderrOut.toString(StandardCharsets.UTF_8));
			}

			execChannel.disconnect();
			session.disconnect();

			return stdoutOut.toByteArray();
		}
		finally
		{
			closeQuietly(session);
		}
	}

	private void closeQuietly(Session session)
	{
		try
		{
			session.disconnect();
		}
		catch (Exception ex)
		{
			log.warn("Closing ssh session failed.", ex);
		}
	}

	private void readResponseInto(Channel channel, ChannelExec execChannel, ByteArrayOutputStream bytesOut) throws IOException
	{
		InputStream in = execChannel.getInputStream();
		byte[] bbuf = new byte[1024];
		while (true)
		{
			while (in.available() > 0)
			{
				int count = in.read(bbuf, 0, 1024);
				if (count < 0) break;
				bytesOut.write(bbuf, 0, count);
			}
			if (channel.isClosed())
			{
				if (in.available() > 0) continue;
				break;
			}
		}
	}
}
