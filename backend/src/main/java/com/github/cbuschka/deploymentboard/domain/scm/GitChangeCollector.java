package com.github.cbuschka.deploymentboard.domain.scm;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.DiffFormatter;
import org.eclipse.jgit.lib.AbbreviatedObjectId;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectReader;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.treewalk.CanonicalTreeParser;
import org.eclipse.jgit.util.io.NullOutputStream;
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
		if (optionalEndCommitish == null)
		{
			return this.gitCommitCollector.collectCommits(git, commitish, true);
		}

		ObjectId oldTree = git.getRepository().resolve(commitish + "^{tree}");
		ObjectId newTree = git.getRepository().resolve(optionalEndCommitish + "^{tree}");

		ObjectReader reader = git.getRepository().newObjectReader();
		CanonicalTreeParser oldTreeIter = new CanonicalTreeParser();
		oldTreeIter.reset(reader, oldTree);
		CanonicalTreeParser newTreeIter = new CanonicalTreeParser();
		newTreeIter.reset(reader, newTree);

		DiffFormatter df = new DiffFormatter(NullOutputStream.INSTANCE);
		df.setRepository(git.getRepository());
		List<DiffEntry> entries = df.scan(oldTreeIter, newTreeIter);
		for (DiffEntry entry : entries)
		{
			AbbreviatedObjectId oldId = entry.getId(DiffEntry.Side.OLD);
			AbbreviatedObjectId newId = entry.getId(DiffEntry.Side.NEW);
			System.out.println(entry+"; "+oldId+" "+newId);
		}

		return Collections.emptySet();
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
