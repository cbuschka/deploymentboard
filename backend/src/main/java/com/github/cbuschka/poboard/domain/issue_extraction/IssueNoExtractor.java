package com.github.cbuschka.poboard.domain.issue_extraction;

import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.stream.Collectors.toList;

@Service
public class IssueNoExtractor
{
	public Set<String> extractIssues(Set<String> issuePrefixes, String comment)
	{
		if (comment == null)
		{
			return Collections.emptySet();
		}

		List<Pattern> patterns = issuePrefixes
				.stream()
				.map(this::toRegex)
				.collect(toList());

		Set<String> issues = new HashSet<>();
		for (Pattern pattern : patterns)
		{
			Matcher matcher = pattern.matcher(comment);

			while (matcher.find())
			{
				issues.add(matcher.group(1));
			}
		}

		return issues;
	}

	private Pattern toRegex(String issuePrefix)
	{
		return Pattern.compile("\\[?(" + issuePrefix.replace("-", "\\-") + "[0-9]+)]?", Pattern.MULTILINE);
	}
}
