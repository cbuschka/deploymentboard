package com.github.cbuschka.deploymentboard.domain.scm;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class Change
{
	private final String commitish;

	private final String comment;
}
