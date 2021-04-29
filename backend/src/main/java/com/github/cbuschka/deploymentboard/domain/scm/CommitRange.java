package com.github.cbuschka.deploymentboard.domain.scm;

import java.util.Objects;

public class CommitRange
{
	private final String start;
	private final String optionalEnd;

	public CommitRange(String start, String optionalEnd)
	{
		this.start = start;
		this.optionalEnd = optionalEnd;
	}

	public String getOptionalEnd()
	{
		return optionalEnd;
	}

	public String getStart()
	{
		return start;
	}

	@Override
	public boolean equals(Object o)
	{
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		CommitRange that = (CommitRange) o;
		return start.equals(that.start) && Objects.equals(optionalEnd, that.optionalEnd);
	}

	@Override
	public int hashCode()
	{
		return Objects.hash(start, optionalEnd);
	}

	@Override
	public String toString()
	{
		return "CommitRange{" +
				"start='" + start + '\'' +
				", optionalEnd='" + optionalEnd + '\'' +
				'}';
	}
}
