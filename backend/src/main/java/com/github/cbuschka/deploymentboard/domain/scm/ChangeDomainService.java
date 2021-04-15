package com.github.cbuschka.deploymentboard.domain.scm;

import lombok.extern.slf4j.Slf4j;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.Repository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.Collections;
import java.util.List;

@Service
@Slf4j
public class ChangeDomainService
{
	@Autowired
	private GitChangeCollector gitChangeCollector;
	@Autowired
	private RepoManager repoManager;

	public List<Change> getChangesFrom(String startCommitish, String optionalEndCommitish, CodeRepository codeRepository)
	{
		if (startCommitish == null)
		{
			return Collections.emptyList();
		}

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
		Repository repo = this.repoManager.updateRepo(codeRepository);

		try (Git git = Git.wrap(repo))
		{
			return this.gitChangeCollector.collectChanges(git, commitish, optionalEndCommitish);
		}
	}

}
