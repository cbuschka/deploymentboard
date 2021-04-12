package com.github.cbuschka.poboard.domain.scm;

import com.github.cbuschka.poboard.domain.auth.PrivateKeyCredentials;
import com.github.cbuschka.poboard.domain.auth.PrivateKeyLoader;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import org.eclipse.jgit.transport.JschConfigSessionFactory;
import org.eclipse.jgit.transport.OpenSshConfig;
import org.eclipse.jgit.util.FS;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class SshSessionContextAwareJschConfigSessionFactory extends JschConfigSessionFactory
{
	@Autowired
	private PrivateKeyLoader privateKeyLoader;

	@Override
	protected void configure(OpenSshConfig.Host hc, Session session)
	{
		List<PrivateKeyCredentials> privateKeyCredentialsList = SshSessionContext.current().getPrivateKeyCredentialsList();
		session.setConfig("StrictHostKeyChecking", "no");
		if (!privateKeyCredentialsList.isEmpty())
		{
			session.setConfig("PreferredAuthentications", "publickey");
		}
	}

	@Override
	protected JSch getJSch(final OpenSshConfig.Host hc, FS fs) throws JSchException
	{
		List<PrivateKeyCredentials> privateKeyCredentialsList = SshSessionContext.current().getPrivateKeyCredentialsList();
		JSch jsch = super.getJSch(hc, fs);
		jsch.removeAllIdentity();
		for (PrivateKeyCredentials c : privateKeyCredentialsList)
		{
			jsch.addIdentity("", privateKeyLoader.getAsciiArmoredBytesUTF8(c), null, null);
		}
		return jsch;
	}
}
