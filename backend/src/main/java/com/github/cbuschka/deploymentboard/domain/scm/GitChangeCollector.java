package com.github.cbuschka.deploymentboard.domain.scm;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

@Component
public class GitChangeCollector
{
	private GitCommitCollector gitCommitCollector;

	public GitChangeCollector(GitCommitCollector gitCommitCollector)
	{
		this.gitCommitCollector = gitCommitCollector;
	}

	public List<Change> collectChanges(Git git, String commitish, String optionalEndCommitish) throws IOException
	{
		Set<String> commitishes = collectCommits(commitish, optionalEndCommitish, git);

		return toChanges(git, commitishes);
	}

	private Set<String> collectCommits(String commitish, String optionalEndCommitish, Git git) throws IOException
	{
		Set<String> baselineCommits = this.gitCommitCollector.collectCommits(git, commitish, true);
		Set<String> excludedCommits = Collections.emptySet();
		if (optionalEndCommitish != null)
		{
			excludedCommits = this.gitCommitCollector.collectCommits(git, optionalEndCommitish, false);
		}
		baselineCommits.removeAll(excludedCommits);
		return baselineCommits;
	}

	private List<Change> toChanges(Git git, Collection<String> commitishes) throws IOException
	{
		List<Change> changes = new ArrayList<>();
		try (RevWalk walk = new RevWalk(git.getRepository()))
		{
			for (String commitish : commitishes)
			{
				RevCommit commit = walk.parseCommit(ObjectId.fromString(commitish));
				String message = commit.getRawBuffer() != null ? commit.getFullMessage() : "";
				changes.add(new Change(commit.getId().getName(), message));
			}

			walk.dispose();

			return changes;
		}
	}
}
