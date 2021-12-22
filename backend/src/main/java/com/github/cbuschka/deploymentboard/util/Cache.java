package com.github.cbuschka.deploymentboard.util;

import java.util.LinkedHashMap;
import java.util.Optional;
import java.util.function.Function;

public class Cache<Key, Value>
{
	private final LRUHashMap<Key, Entry<Value>> entries;

	public Cache()
	{
		this(100);
	}

	public Cache(int maxSize)
	{
		this.entries = new LRUHashMap<>(maxSize);
	}

	public Value get(Key key, int expiryMillis, Function<Key, Optional<Value>> loadingFunction, Function<Key, Value> defaultSupplier)
	{
		synchronized (entries)
		{
			Entry<Value> entry = this.entries.get(key);
			if (entry != null && !entry.hasExpired(expiryMillis))
			{
				return entry.value;
			}
		}

		return loadingFunction.apply(key)
				.stream()
				.peek((v) -> {
					synchronized (entries)
					{
						this.entries.put(key, new Entry<>(v, System.currentTimeMillis()));
					}
				})
				.findFirst()
				.orElseGet(() -> defaultSupplier.apply(key));
	}

	public int getEntryCount() {
		synchronized(entries) {
			return this.entries.size();
		}
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

	private static class LRUHashMap<K, V> extends LinkedHashMap<K, V>
	{
		private final int maxSize;

		@Override
		protected boolean removeEldestEntry(java.util.Map.Entry<K, V> eldest)
		{
			return size() > this.maxSize;
		}

		public LRUHashMap(int maxSize)
		{
			super(maxSize + 1, 1.0f, true);
			this.maxSize = maxSize;
		}
	}
}
