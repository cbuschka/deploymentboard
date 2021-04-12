package com.github.cbuschka.poboard.business.dashboard;

import com.github.cbuschka.poboard.domain.issue_extraction.IssueNoExtractor;
import com.github.cbuschka.poboard.domain.scm.Change;
import com.github.cbuschka.poboard.domain.scm.ChangeDomainService;
import com.github.cbuschka.poboard.domain.scm.CodeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class GetIssuesByChangeBusinessService
{
	@Autowired
	private ChangeDomainService changeDomainService;
	@Autowired
	private IssueNoExtractor issueNoExtractor;

	public Set<String> getIssuesFor(Set<String> issuePrefixes, String commitish, CodeRepository codeRepository)
	{
		List<Change> changes = this.changeDomainService.getChangesFrom(commitish, codeRepository);
		return changes.stream()
				.map(Change::getComment)
				.map((comment) -> issueNoExtractor.extractIssues(issuePrefixes, comment))
				.flatMap(Set::stream)
				.filter(Objects::nonNull)
				.collect(Collectors.toSet());
	}
}
