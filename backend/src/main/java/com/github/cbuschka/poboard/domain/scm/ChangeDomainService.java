package com.github.cbuschka.poboard.domain.scm;

import com.github.cbuschka.poboard.domain.auth.AuthDomainService;
import com.github.cbuschka.poboard.domain.auth.PrivateKeyCredentials;
import com.github.cbuschka.poboard.domain.auth.UsernamePasswordCredentials;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.eclipse.jgit.transport.CredentialsProvider;
import org.eclipse.jgit.transport.JschConfigSessionFactory;
import org.eclipse.jgit.transport.OpenSshConfig;
import org.eclipse.jgit.transport.SshSessionFactory;
import org.eclipse.jgit.transport.URIish;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.eclipse.jgit.util.FS;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

@Service
public class ChangeDomainService
{
	@Autowired
	private AuthDomainService authDomainService;
	@Value("/tmp/poboard-workspace")
	private File workspaceDir;

	@PostConstruct
	public void init()
	{
		this.workspaceDir.mkdirs();
	}

	public List<Change> getChangesFrom(String startCommitish, String optionalEndCommitish, CodeRepository codeRepository)
	{
		try
		{
			return listChanges(startCommitish, optionalEndCommitish, codeRepository);
		}
		catch (Exception ex)
		{
			throw new RuntimeException(ex);
		}
	}

	private List<Change> listChanges(String commitish, String optionalEndCommitish, CodeRepository codeRepository) throws Exception
	{
		if (commitish == null)
		{
			return Collections.emptyList();
		}

		URIish repositoryUri = new URIish(codeRepository.getUrl());
		int lastSlash = codeRepository.getUrl().lastIndexOf("/");
		String repoName = codeRepository.getUrl().substring(lastSlash + 1);
		if (!repoName.endsWith(".git"))
		{
			repoName = repoName + ".git";
		}

		File repoDir = new File(this.workspaceDir, repoName);

		Repository repo;
		if (!repoDir.isDirectory())
		{
			@SuppressWarnings("unused")
			boolean created = repoDir.mkdirs();
			repo = FileRepositoryBuilder.create(repoDir);
			repo.create(true);
		}
		else
		{
			repo = new FileRepositoryBuilder()
					.setGitDir(repoDir)
					.build();
		}

		List<PrivateKeyCredentials> privateKeyCredentialsList = authDomainService.getPrivateKeyCredentials(repositoryUri.getUser(), repositoryUri.getHost());
		SshSessionFactory.setInstance(new JschConfigSessionFactory()
		{
			@Override
			protected void configure(OpenSshConfig.Host hc, Session session)
			{
				session.setConfig("StrictHostKeyChecking", "no");
				if (!privateKeyCredentialsList.isEmpty())
				{
					session.setConfig("PreferredAuthentications", "publickey");
				}
			}

			@Override
			protected JSch getJSch(final OpenSshConfig.Host hc, FS fs) throws JSchException
			{
				JSch jsch = super.getJSch(hc, fs);
				jsch.removeAllIdentity();
				for (PrivateKeyCredentials credentials : privateKeyCredentialsList)
				{
					jsch.addIdentity("", credentials.getData().getBytes(StandardCharsets.UTF_8), null, null);
				}
				return jsch;
			}
		});

		CredentialsProvider credentialsProvider = getCredentialsProvider(repositoryUri);
		try (Git git = Git.wrap(repo))
		{
			git.remoteAdd().setName("origin").setUri(repositoryUri).call();
			git.remoteSetUrl().setRemoteName("origin").setRemoteUri(repositoryUri);
			git.fetch()
					.setCredentialsProvider(credentialsProvider)
					.setRemote("origin").call();

			ObjectId optionalEndObjectId = optionalEndCommitish != null ? ObjectId.fromString(optionalEndCommitish) : null;
			List<Change> changes = new ArrayList<>();
			try (RevWalk walk = new RevWalk(repo))
			{
				RevCommit startCommit = walk.parseCommit(ObjectId.fromString(commitish));
				List<RevCommit> traversalQueue = new LinkedList<>();
				traversalQueue.add(0, startCommit);

				Set<ObjectId> seenSet = new HashSet<>();
				while (!traversalQueue.isEmpty())
				{
					RevCommit commit = traversalQueue.remove(0);
					if (optionalEndObjectId != null && optionalEndObjectId.equals(commit.getId()))
					{
						continue;
					}

					if (seenSet.contains(commit.getId()))
					{
						continue;
					}

					String message = commit.getRawBuffer() != null ? commit.getFullMessage() : "";
					for (int i = 0; commit.getParents() != null && i < commit.getParentCount(); ++i)
					{
						RevCommit parent = commit.getParent(commit.getParentCount() - i - 1);
						traversalQueue.add(parent);
					}
					changes.add(new Change(commit.getId().toString(), message));
					seenSet.add(commit.getId());
				}

				walk.dispose();
			}

			return changes;
		}
	}

	private CredentialsProvider getCredentialsProvider(URIish remoteUri)
	{
		CredentialsProvider credentialsProvider = null;
		List<UsernamePasswordCredentials> usernamePasswordCredentialsList = this.authDomainService.getUsernamePasswordCredentials(remoteUri.getUser(), remoteUri.getHost());
		if (usernamePasswordCredentialsList != null && !usernamePasswordCredentialsList.isEmpty())
		{
			UsernamePasswordCredentials usernamePasswordCredentials = usernamePasswordCredentialsList.get(0);
			credentialsProvider = new UsernamePasswordCredentialsProvider(usernamePasswordCredentials.getUsername(), usernamePasswordCredentials.getPassword());
		}
		return credentialsProvider;
	}
}
