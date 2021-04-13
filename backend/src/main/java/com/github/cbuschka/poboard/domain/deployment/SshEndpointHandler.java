package com.github.cbuschka.poboard.domain.deployment;

import com.github.cbuschka.poboard.domain.auth.AuthDomainService;
import com.github.cbuschka.poboard.domain.auth.PrivateKeyCredentials;
import com.github.cbuschka.poboard.domain.auth.PrivateKeyLoader;
import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.jgit.transport.URIish;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.List;

@Component
@Slf4j
public class SshEndpointHandler implements EndpointHandler
{
	@Autowired
	private AuthDomainService authDomainService;
	@Autowired
	private DeploymentInfoExtractor deploymentInfoExtractor;
	@Autowired
	private PrivateKeyLoader privateKeyLoader;

	@Override
	public boolean handles(Endpoint endpoint)
	{
		return endpoint.getUrl().startsWith("ssh://");
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
				jSch.addIdentity(uri.getUser(), privateKeyLoader.getAsciiArmoredBytesUTF8(c), null, privateKeyLoader.getPasswordBytesUTF8(c));
			}

			Session session = jSch.getSession(uri.getUser(), uri.getHost(), uri.getPort() != -1 ? uri.getPort() : 22);
			session.setTimeout(3_000);
			session.setConfig("StrictHostKeyChecking", "no");
			session.connect();

			Channel channel = session.openChannel("exec");
			ChannelExec execChannel = (ChannelExec) channel;
			execChannel.setCommand(endpoint.getCommand());
			execChannel.setInputStream(null);
			execChannel.setErrStream(java.lang.System.err, true);
			ByteArrayOutputStream bytesOut = new ByteArrayOutputStream();
			execChannel.connect();
			execChannel.start();

			readResponseInto(channel, execChannel, bytesOut);

			int exitStatus = execChannel.getExitStatus();
			if (exitStatus != 0)
			{
				return DeploymentInfo.failure(system, env, "Command exit code: " + exitStatus);
			}

			execChannel.disconnect();
			session.disconnect();

			return this.deploymentInfoExtractor.extractDeploymentInfoFrom(new ByteArrayInputStream(bytesOut.toByteArray()), system, env, endpoint);
		}
		catch (IOException | URISyntaxException | JSchException ex)
		{
			if (ex.getMessage().contains("Connection refused"))
			{
				return DeploymentInfo.unreachable(system, env, "Connection refused.");
			}

			log.error("Getting deployment info for {} failed.", endpoint.getUrl(), ex);

			return DeploymentInfo.failure(system, env, ex.getMessage());
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
