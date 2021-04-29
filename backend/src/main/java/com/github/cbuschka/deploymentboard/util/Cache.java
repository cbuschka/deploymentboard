package com.github.cbuschka.deploymentboard.util;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

public class Cache<Key, Value>
{
	private final Map<Key, Entry<Value>> entries = new ConcurrentHashMap<>();

	public Value get(Key key, int expiryMillis, Function<Key, Optional<Value>> loadingFunction, Function<Key, Value> defaultSupplier)
	{
		Entry<Value> entry = this.entries.get(key);
		if (entry != null && !entry.hasExpired(expiryMillis))
		{
			return entry.value;
		}

		return loadingFunction.apply(key)
				.stream()
				.peek((v) -> this.entries.put(key, new Entry<>(v, System.currentTimeMillis())))
				.findFirst()
				.orElseGet(() -> defaultSupplier.apply(key));
	}


	private static class Entry<Value>
	{
		private final Value value;
		private final long loadTimeMillis;

		public Entry(Value value, long loadTimeMillis)
		{
			this.value = value;
			this.loadTimeMillis = loadTimeMillis;
		}

		private boolean hasExpired(int expiryMillis)
		{
			return System.currentTimeMillis() > this.loadTimeMillis + expiryMillis;
		}
	}
}
