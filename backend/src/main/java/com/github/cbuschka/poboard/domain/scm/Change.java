package com.github.cbuschka.poboard.domain.scm;

import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class Change
{
	private String commitish;

	private String predecessor;

	private String comment;
}
