package com.github.cbuschka.poboard.domain.scm;

import com.github.cbuschka.poboard.domain.auth.PrivateKeyCredentials;
import org.aspectj.apache.bcel.classfile.Code;
import org.eclipse.jgit.transport.URIish;

import java.util.List;
import java.util.concurrent.Callable;

class SshSessionContext<V>
{
	private static final ThreadLocal<SshSessionContext> threadLocal = new ThreadLocal<>();

	private CodeRepository codeRepository;

	private final URIish repoUri;

	private final List<PrivateKeyCredentials> privateKeyCredentialsList;

	public static <V> SshSessionContext<V> current()
	{
		@SuppressWarnings("unchecked")
		SshSessionContext<V> context = threadLocal.get();
		if (context == null)
		{
			throw new IllegalStateException("No current context.");
		}

		return context;
	}

	public SshSessionContext(URIish repoUri, CodeRepository codeRepository, List<PrivateKeyCredentials> privateKeyCredentialsList)
	{
		this.repoUri = repoUri;
		this.privateKeyCredentialsList = privateKeyCredentialsList;
	}

	public List<PrivateKeyCredentials> getPrivateKeyCredentialsList()
	{
		return privateKeyCredentialsList;
	}

	public CodeRepository getCodeRepository()
	{
		return codeRepository;
	}

	public URIish getRepoUri()
	{
		return repoUri;
	}

	public V run(Callable<V> r) throws Exception
	{
		SshSessionContext<?> oldContext = threadLocal.get();
		try
		{
			threadLocal.set(this);
			return r.call();
		}
		finally
		{
			if (oldContext != null)
			{
				threadLocal.set(oldContext);
			}
			else
			{
				threadLocal.remove();
			}
		}
	}
}
