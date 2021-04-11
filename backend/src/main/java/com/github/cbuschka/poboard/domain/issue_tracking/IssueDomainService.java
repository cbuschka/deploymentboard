package com.github.cbuschka.poboard.domain.issue_tracking;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class IssueDomainService
{
	@Autowired
	private ProjectDomainService projectDomainService;

	public IssueStatus getIssueStatus(String issueCode)
	{
		return this.projectDomainService.getProjectFor(issueCode)
				.map((project) -> getIssueStatusFor(issueCode, project))
				.orElse(IssueStatus.UNKNOWN);
	}

	private IssueStatus getIssueStatusFor(String issueCode, Project project)
	{
		return IssueStatus.OPEN;
	}
}
