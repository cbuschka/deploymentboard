package com.github.cbuschka.deploymentboard.util;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class Cache<Key, Value>
{
	private final Map<Key, Entry<Value>> entries = new ConcurrentHashMap<>();

	public void put(Key key, Value value)
	{
		this.entries.put(key, new Entry<>(value, System.currentTimeMillis()));
	}

	public Optional<Value> get(Key key, int expiryMillis)
	{
		Entry<Value> entry = this.entries.get(key);
		if (entry == null || entry.hasExpired(expiryMillis))
		{
			this.entries.remove(key);
			return Optional.empty();
		}

		return Optional.of(entry.value);
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
