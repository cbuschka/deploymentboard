package com.github.cbuschka.poboard.util;

public class Integers
{
	public static int firstNonNull(Integer... values)
	{
		for (Integer value : values)
		{
			if (value != null)
			{
				return value;
			}
		}

		throw new IllegalStateException("No non null value.");
	}
}
