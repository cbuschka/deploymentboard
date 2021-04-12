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
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

	public List<Change> getChangesFrom(String startCommitish, String endCommitish, CodeRepository codeRepository)
	{
		try
		{
			Map<String, Change> changesByCommitish = listChanges(startCommitish, endCommitish, codeRepository)
					.stream()
					.collect(Collectors.toMap(Change::getCommitish, p -> p, (p, q) -> p));
			List<Change> changes = new ArrayList<>();
			Change curr = changesByCommitish.get(startCommitish);
			while (curr != null)
			{
				changes.add(curr);
				curr = changesByCommitish.get(curr.getPredecessor());
			}

			return changes;
		}
		catch (Exception ex)
		{
			throw new RuntimeException(ex);
		}
	}

	private List<Change> listChanges(String startCommitish, String endCommitish, CodeRepository codeRepository) throws Exception
	{
		if (startCommitish == null)
		{
			return Collections.emptyList();
		}

		File repoDir = new File(this.workspaceDir, codeRepository.getUuid() + ".git");

		URIish repositoryUri = new URIish(codeRepository.getUrl());

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

		SshSessionFactory.setInstance(new JschConfigSessionFactory()
		{
			@Override
			protected void configure(OpenSshConfig.Host hc, Session session)
			{
				session.setConfig("StrictHostKeyChecking", "no");
			}

			@Override
			protected JSch getJSch(final OpenSshConfig.Host hc, FS fs) throws JSchException
			{
				JSch jsch = super.getJSch(hc, fs);
				jsch.removeAllIdentity();
				for (PrivateKeyCredentials credentials : authDomainService.getPrivateKeyCredentials(repositoryUri.getUser(), hc.getHostName()))
				{
					jsch.addIdentity("", credentials.getData().getBytes(StandardCharsets.UTF_8), null, null);
				}
				return jsch;
			}
		});

		try (Git git = Git.wrap(repo))
		{
			git.remoteAdd().setName("origin").setUri(repositoryUri).call();
			URIish remoteUri = new URIish(codeRepository.getUrl());
			git.remoteSetUrl().setRemoteName("origin").setRemoteUri(remoteUri);
			CredentialsProvider credentialsProvider = null;
			List<UsernamePasswordCredentials> usernamePasswordCredentialsList = this.authDomainService.getUsernamePasswordCredentials(remoteUri.getUser(), remoteUri.getHost());
			if (usernamePasswordCredentialsList != null && !usernamePasswordCredentialsList.isEmpty())
			{
				UsernamePasswordCredentials usernamePasswordCredentials = usernamePasswordCredentialsList.get(0);
				credentialsProvider = new UsernamePasswordCredentialsProvider(usernamePasswordCredentials.getUsername(), usernamePasswordCredentials.getPassword());
			}
			git.fetch()
					.setCredentialsProvider(credentialsProvider)
					.setRemote("origin").call();

			ObjectId endCommitId = endCommitish != null ? ObjectId.fromString(endCommitish) : null;
			List<Change> changes = new ArrayList<>();
			try (RevWalk walk = new RevWalk(repo))
			{
				RevCommit commit = walk.parseCommit(ObjectId.fromString(startCommitish));
				while (commit != null && (endCommitId == null || endCommitId.equals(commit.getId())))
				{
					String message = commit.getRawBuffer() != null ? commit.getFullMessage() : "";
					RevCommit parent = commit.getParents() != null && commit.getParentCount() > 0 ? commit.getParent(0) : null; // FIXME merge commits?
					changes.add(new Change(commit.getId().toString(), parent != null ? parent.getId().toString() : null, message));
					commit = parent;
				}

				walk.dispose();
			}

			return changes;
		}
	}
}
