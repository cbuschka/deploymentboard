package com.github.cbuschka.poboard.domain.scm;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Getter
public class Change
{
	private final String commitish;

	private final String predecessor;

	private final String comment;

	public int hashCode()
	{
		return this.commitish.hashCode();
	}

	@Override
	public boolean equals(Object o)
	{
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Change change = (Change) o;
		return commitish.equals(change.commitish);
	}
}
