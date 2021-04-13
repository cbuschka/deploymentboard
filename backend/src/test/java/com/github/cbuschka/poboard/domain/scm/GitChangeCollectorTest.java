package com.github.cbuschka.poboard.domain.scm;

import org.eclipse.jgit.api.errors.GitAPIException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class GitChangeCollectorTest
{
	private static final String FIRST_COMMENT = "ISSUE-1";
	private static final String SECOND_COMMENT = "ISSUE-2";
	private static final String THIRD_COMMENT = "ISSUE-3";

	private final GitRepoHolder gitRepoHolder = new GitRepoHolder();

	@InjectMocks
	private GitChangeCollector gitChangeCollector;

	private List<Change> changes;

	@Test
	void allCommits() throws Exception
	{
		givenGitRepoWithThreeCommits();

		whenAllChangesCollected();

		assertThat(changes.stream().map(Change::getComment).collect(Collectors.toList())).containsExactly(THIRD_COMMENT, SECOND_COMMENT, FIRST_COMMENT);
	}

	@Test
	void twoCommitsFromBottom() throws Exception
	{
		givenGitRepoWithThreeCommits();

		whenChangesCollectedBetween(1, null);

		assertThat(changes.stream().map(Change::getComment).collect(Collectors.toList())).containsExactly(SECOND_COMMENT, FIRST_COMMENT);
	}

	@Test
	void twoCommitsFromTop() throws Exception
	{
		givenGitRepoWithThreeCommits();

		whenChangesCollectedBetween(2, 0);

		assertThat(changes.stream().map(Change::getComment).collect(Collectors.toList())).containsExactly(THIRD_COMMENT, SECOND_COMMENT);
	}

	private void whenAllChangesCollected() throws Exception
	{
		this.changes = this.gitChangeCollector.collectChanges(this.gitRepoHolder.getGit(), this.gitRepoHolder.getHead().getName(), null);
	}

	private void whenChangesCollectedBetween(int start, Integer end) throws Exception
	{
		this.changes = this.gitChangeCollector.collectChanges(this.gitRepoHolder.getGit(), this.gitRepoHolder.getCommitish(start).getName(), end != null ? this.gitRepoHolder.getCommitish(end).getName() : null);
	}

	@AfterEach
	public void cleanup()
	{
		this.gitRepoHolder.destroy();
	}

	private void givenGitRepoWithThreeCommits() throws IOException, GitAPIException
	{
		this.gitRepoHolder.newRepo()
				.add("message.txt", "hello world!", FIRST_COMMENT)
				.add("message.txt", "hello world! how are you?", SECOND_COMMENT)
				.remove("message.txt", THIRD_COMMENT);
	}
}
