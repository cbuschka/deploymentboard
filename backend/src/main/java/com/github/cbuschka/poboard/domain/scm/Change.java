package com.github.cbuschka.poboard.domain.scm;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class Change
{
	private String commitish;

	private String comment;
}
