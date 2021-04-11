package com.github.cbuschka.poboard.domain.scm;

import com.github.cbuschka.poboard.domain.mock.MockDataProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ChangeDomainService
{
	@Autowired
	private MockDataProvider mockDataProvider;

	public List<Change> getChangesFrom(String commitish)
	{
		Map<String, Change> changesByCommitish = this.mockDataProvider.getMockData().changes
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
