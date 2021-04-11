package com.github.cbuschka.poboard.business.dashboard;

import com.github.cbuschka.poboard.domain.scm.Change;
import com.github.cbuschka.poboard.domain.scm.ChangeDomainService;
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

	public Set<String> getIssuesFor(String commitish)
	{
		List<Change> changes = this.changeDomainService.getChangesFrom(commitish);
		return changes.stream()
				.map(Change::getIssueNo)
				.filter(Objects::nonNull)
				.collect(Collectors.toSet());
	}
}
