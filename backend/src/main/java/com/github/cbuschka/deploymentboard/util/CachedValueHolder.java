package com.github.cbuschka.deploymentboard.util;

import java.util.concurrent.Executor;
import java.util.function.Supplier;

public class CachedValueHolder<ValueType>
{
	private final Executor executor;
	private final Supplier<ValueType> loader;

	private long loadTimeMillis = 0L;
	private ValueType value;
	private boolean reloading = false;

	public CachedValueHolder(Supplier<ValueType> loader, Executor executor)
	{
		this.loader = loader;
		this.executor = executor;
	}

	public synchronized ValueType get(int expiryMillis)
	{
		if (this.value == null)
		{
			this.value = this.loader.get();
		}

		if (hasExpired(expiryMillis) && !reloading)
		{
			this.executor.execute(this::reload);
		}

		return this.value;
	}

	private synchronized void set(ValueType value)
	{
		this.value = value;
		this.loadTimeMillis = System.currentTimeMillis();
		this.reloading = false;
	}

	private void reload()
	{
		ValueType value = loader.get();
		set(value);
	}

	private boolean hasExpired(int expiryMillis)
	{
		return System.currentTimeMillis() > this.loadTimeMillis + expiryMillis;
	}
}
