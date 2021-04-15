package com.github.cbuschka.deploymentboard.domain.scm;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

@Component
public class GitCommitCollector
{
	public Set<String> collectCommits(Git git, String commitish, boolean ordered) throws IOException
	{
		ObjectId startObjectId = ObjectId.fromString(commitish);

		return collectCommitsByCommitWalk(git, startObjectId, ordered);
	}

	private Set<String> collectCommitsByCommitWalk(Git git, ObjectId startObjectId, boolean ordered) throws IOException
	{
		Set<String> commits = ordered ? new LinkedHashSet<>() : new HashSet<>();
		try (RevWalk walk = new RevWalk(git.getRepository()))
		{
			RevCommit startCommit = walk.parseCommit(startObjectId);
			List<ObjectId> traversalQueue = new LinkedList<>();
			traversalQueue.add(0, startCommit);

			Set<ObjectId> seenSet = new HashSet<>();
			while (!traversalQueue.isEmpty())
			{
				RevCommit commit = walk.parseCommit(traversalQueue.remove(0));
				if (seenSet.contains(commit.getId()))
				{
					continue;
				}

				for (int i = 0; commit.getParents() != null && i < commit.getParentCount(); ++i)
				{
					RevCommit parent = commit.getParent(commit.getParentCount() - i - 1);
					traversalQueue.add(parent.getId());
				}

				seenSet.add(commit.getId());
				commits.add(commit.getId().getName());
			}

			walk.dispose();

			return commits;
		}
	}
}
