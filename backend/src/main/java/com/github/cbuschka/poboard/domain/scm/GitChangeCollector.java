package com.github.cbuschka.poboard.domain.scm;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

@Component
public class GitChangeCollector
{
	public List<Change> collectChanges(Git git, String commitish, String optionalEndCommitish) throws Exception
	{
		ObjectId optionalEndObjectId = optionalEndCommitish != null ? ObjectId.fromString(optionalEndCommitish) : null;
		List<Change> changes = new ArrayList<>();
		try (RevWalk walk = new RevWalk(git.getRepository()))
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

			return changes;
		}
	}
}
