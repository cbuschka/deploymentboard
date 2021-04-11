package com.github.cbuschka.poboard.domain.issue_extraction;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class IssueNoExtractorTest
{
	private IssueNoExtractor issueNoExtractor = new IssueNoExtractor();
	private Set<String> issuePrefixes;
	private Set<String> extractedIssueNos;
	private String comment;

	@Test
	void onlyIssueNo()
	{
		givenIssuePrefixes("ABC-");
		givenComment("ABC-1");

		whenExtracted();

		thenIssueNosIs("ABC-1");
	}

	@Test
	void bracketedIssueNo()
	{
		givenIssuePrefixes("ABC-");
		givenComment("[ABC-1]");

		whenExtracted();

		thenIssueNosIs("ABC-1");
	}

	@Test
	void embeddedIssueNo()
	{
		givenIssuePrefixes("ABC-");
		givenComment("barABC-123foo");

		whenExtracted();

		thenIssueNosIs("ABC-123");
	}

	@Test
	void multipleIssueNos()
	{
		givenIssuePrefixes("ABC-");
		givenComment("ABC-1 ABC-2");

		whenExtracted();

		thenIssueNosIs("ABC-1", "ABC-2");
	}

	@Test
	void multipleBracketedIssueNos()
	{
		givenIssuePrefixes("ABC-");
		givenComment("[ABC-1] [ABC-2]");

		whenExtracted();

		thenIssueNosIs("ABC-1", "ABC-2");
	}

	private void givenComment(String comment)
	{
		this.comment = comment;
	}

	private void whenExtracted()
	{
		extractedIssueNos = issueNoExtractor.extractIssues(issuePrefixes, comment);
	}

	private void thenIssueNosIs(String... issueNos)
	{
		assertThat(extractedIssueNos).contains(issueNos);
	}

	private void givenIssuePrefixes(String... ps)
	{
		this.issuePrefixes = new HashSet<>(Arrays.asList(ps));
	}
}