package com.github.cbuschka.poboard.domain.scm;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ChangeDomainService
{
	public List<Change> getChangesFrom(String commitish, CodeRepository codeRepository)
	{
		Map<String, Change> changesByCommitish = codeRepository.getChanges()
				.stream()
				.collect(Collectors.toMap(Change::getCommitish, p -> p, (p, q) -> p));
		List<Change> changes = new ArrayList<>();
		Change curr = changesByCommitish.get(commitish);
		while (curr != null)
		{
			changes.add(curr);
			curr = changesByCommitish.get(curr.getPredecessor());
		}

		return changes;
	}
}
