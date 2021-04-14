package com.github.cbuschka.deploymentboard.util;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class Collections
{
	public static <T> Set<T> combined(Collection<T>... cols)
	{
		Set<T> set = new HashSet<>();
		for (Collection<T> col : cols)
		{
			if (col != null)
			{
				set.addAll(col);
			}
		}

		return set;
	}
}
