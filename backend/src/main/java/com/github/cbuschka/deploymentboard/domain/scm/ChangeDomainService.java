package com.github.cbuschka.deploymentboard.domain.scm;

import com.github.cbuschka.deploymentboard.util.Cache;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.Repository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@Slf4j
public class ChangeDomainService
{
	@Autowired
	private List<GitChangeCollector> gitChangeCollectors;
	@Autowired
	private RepoManager repoManager;

	private final Cache<CommitRange, List<Change>> changeCache = new Cache<>();

	public List<Change> getChangesFrom(String startCommitish, String optionalEndCommitish, CodeRepository codeRepository)
	{
		if (startCommitish == null)
		{
			return Collections.emptyList();
		}

		return this.changeCache.get(new CommitRange(startCommitish, optionalEndCommitish), codeRepository.getRecheckTimeoutMillis(),
				(cr) -> getChangesInternal(cr, codeRepository),
				(cr) -> Collections.emptyList()
		);
	}

	private Optional<List<Change>> getChangesInternal(CommitRange commitRange, CodeRepository codeRepository)
	{
		try
		{
			return Optional.of(listChanges(commitRange.getStart(), commitRange.getOptionalEnd(), codeRepository));
		}
		catch (Exception ex)
		{
			log.error("Error listing changes for {}.", codeRepository.getUrl(), ex);

			return Optional.empty();
		}
	}

	private List<Change> listChanges(String commitish, String optionalEndCommitish, CodeRepository codeRepository) throws Exception
	{
		try (Repository repo = this.repoManager.openRepo(codeRepository);)
		{
			try (Git git = Git.wrap(repo))
			{
				GitChangeCollector handler = getHandlerFor(ChangeDetectionAlgorithm.simple);
				return Collections.unmodifiableList(handler.collectChanges(git, commitish, optionalEndCommitish));
			}
		}
	}

	private GitChangeCollector getHandlerFor(ChangeDetectionAlgorithm algorithm)
	{
		return this.gitChangeCollectors
				.stream()
				.filter((h) -> h.handles(algorithm))
				.findFirst()
				.orElseThrow(() -> new NoSuchElementException("No handler for " + algorithm + "."));
	}

}
