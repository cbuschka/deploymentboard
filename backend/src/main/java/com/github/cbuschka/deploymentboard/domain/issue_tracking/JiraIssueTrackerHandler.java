package com.github.cbuschka.deploymentboard.domain.issue_tracking;

import com.github.cbuschka.deploymentboard.domain.auth.AuthDomainService;
import com.github.cbuschka.deploymentboard.domain.auth.PasswordCredentials;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.jgit.transport.URIish;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

@Slf4j
@Component
public class JiraIssueTrackerHandler implements IssueTrackerHandler
{
	private JiraClient jiraClient = new JiraClient();

	@Autowired
	private AuthDomainService authDomainService;

	@Override
	public boolean handles(IssueTracker issueTracker)
	{
		return issueTracker.type == IssueTrackerType.jira;
	}

	@Override
	public Optional<IssueInfo> getIssueInfoFor(String issueNo, IssueTracker issueTracker)
	{
		try
		{
			URIish urIish = new URIish(issueTracker.url);
			List<PasswordCredentials> usernamePasswordCredentials = this.authDomainService.getUsernamePasswordCredentials(issueTracker.username, urIish.getHost());

			return  jiraClient.getIssueInfo(issueTracker.url, issueNo, issueTracker.username,
					usernamePasswordCredentials.isEmpty() ? null : usernamePasswordCredentials.get(0));
		}
		catch (IOException | URISyntaxException ex)
		{
			log.warn("Getting issue failed.", ex);

			return Optional.empty();
		}
	}
}
