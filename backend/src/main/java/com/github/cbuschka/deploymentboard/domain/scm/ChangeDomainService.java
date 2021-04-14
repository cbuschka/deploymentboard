package com.github.cbuschka.deploymentboard.domain.scm;

import com.github.cbuschka.deploymentboard.domain.auth.AuthDomainService;
import com.github.cbuschka.deploymentboard.domain.auth.PasswordCredentials;
import com.github.cbuschka.deploymentboard.domain.auth.PrivateKeyCredentials;
import com.github.cbuschka.deploymentboard.domain.config.ConfigProvider;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.eclipse.jgit.transport.CredentialsProvider;
import org.eclipse.jgit.transport.JschConfigSessionFactory;
import org.eclipse.jgit.transport.URIish;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

@Service
@Slf4j
public class ChangeDomainService
{
	@Autowired
	private AuthDomainService authDomainService;
	@Autowired
	private ConfigProvider configProvider;
	@Autowired
	private SshSessionContextAwareJschConfigSessionFactory sshSessionContextAwareJschConfigSessionFactory;
	@Autowired
	private GitChangeCollector gitChangeCollector;

	private File repositoriesDir;

	@PostConstruct
	public void init()
	{
		this.repositoriesDir = new File(new File(this.configProvider.getConfig().workspace.getDir()), "repositories");
		@SuppressWarnings("unused")
		boolean done = this.repositoriesDir.mkdirs();

		JschConfigSessionFactory.setInstance(sshSessionContextAwareJschConfigSessionFactory);
	}

	public List<Change> getChangesFrom(String startCommitish, String optionalEndCommitish, CodeRepository codeRepository)
	{
		try
		{
			return listChanges(startCommitish, optionalEndCommitish, codeRepository);
		}
		catch (Exception ex)
		{
			log.error("Error listing changes for {}.", codeRepository.getUrl(), ex);

			return Collections.emptyList();
		}
	}

	private List<Change> listChanges(String commitish, String optionalEndCommitish, CodeRepository codeRepository) throws Exception
	{
		if (commitish == null)
		{
			return Collections.emptyList();
		}

		URIish repositoryUri = new URIish(codeRepository.getUrl());

		Repository repo = createRepositoryIfNotExists(codeRepository);
		List<PrivateKeyCredentials> privateKeyCredentialsList = this.authDomainService.getPrivateKeyCredentials(repositoryUri.getUser(), repositoryUri.getHost());
		return new SshSessionContext<List<Change>>(repositoryUri, codeRepository, privateKeyCredentialsList)
				.run(() -> listChanges(repositoryUri, commitish, optionalEndCommitish, repo));
	}

	private List<Change> listChanges(URIish repositoryUri, String commitish, String optionalEndCommitish, Repository repo) throws Exception
	{
		CredentialsProvider credentialsProvider = getCredentialsProvider(repositoryUri);
		try (Git git = Git.wrap(repo))
		{
			updateRepo(repositoryUri, credentialsProvider, git);

			return this.gitChangeCollector.collectChanges(git, commitish, optionalEndCommitish);
		}
	}

	private void updateRepo(URIish repositoryUri, CredentialsProvider credentialsProvider, Git git) throws GitAPIException
	{
		log.debug("Updating repo {}...", repositoryUri);

		git.remoteAdd().setName("origin").setUri(repositoryUri).call();
		git.remoteSetUrl().setRemoteName("origin").setRemoteUri(repositoryUri);
		git.fetch()
				.setCredentialsProvider(credentialsProvider)
				.setRemote("origin").call();

		log.debug("Updated repo {}.", repositoryUri);
	}

	private Repository createRepositoryIfNotExists(CodeRepository codeRepository) throws IOException
	{
		File repoDir = getWorkspaceRepositoryDir(codeRepository);

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
		return repo;
	}

	private File getWorkspaceRepositoryDir(CodeRepository codeRepository)
	{
		int lastSlash = codeRepository.getUrl().lastIndexOf("/");
		String repoName = codeRepository.getUrl().substring(lastSlash + 1);
		if (!repoName.endsWith(".git"))
		{
			repoName = repoName + ".git";
		}

		File repoDir = new File(this.repositoriesDir, repoName);
		return repoDir;
	}

	private CredentialsProvider getCredentialsProvider(URIish remoteUri)
	{
		CredentialsProvider credentialsProvider = null;
		List<PasswordCredentials> usernamePasswordCredentialsList = this.authDomainService.getUsernamePasswordCredentials(remoteUri.getUser(), remoteUri.getHost());
		if (usernamePasswordCredentialsList != null && !usernamePasswordCredentialsList.isEmpty())
		{
			PasswordCredentials usernamePasswordCredentials = usernamePasswordCredentialsList.get(0);
			credentialsProvider = new UsernamePasswordCredentialsProvider(remoteUri.getUser(), usernamePasswordCredentials.getPassword());
		}
		return credentialsProvider;
	}
}
