package com.github.cbuschka.poboard.util;

import java.util.concurrent.Executor;
import java.util.function.Supplier;

public class CachedValueHolder<ValueType>
{
	private final Executor executor;
	private final Supplier<ValueType> loader;
	private final int reloadMillis;

	private long expiryMillis = 0L;
	private ValueType value;
	private boolean reloading = false;

	public CachedValueHolder(Supplier<ValueType> loader, int reloadMillis, Executor executor)
	{
		this.loader = loader;
		this.reloadMillis = reloadMillis;
		this.executor = executor;
	}

	public synchronized ValueType get()
	{
		if (this.value == null)
		{
			this.value = this.loader.get();
		}

		if (isExpired() && !reloading)
		{
			this.executor.execute(this::reload);
		}

		return this.value;
	}

	private synchronized void set(ValueType value)
	{
		this.value = value;
		this.expiryMillis = System.currentTimeMillis() + this.reloadMillis;
		this.reloading = false;
	}

	private void reload()
	{
		ValueType value = loader.get();
		set(value);
	}

	private boolean isExpired()
	{
		return System.currentTimeMillis() > this.expiryMillis;
	}
}
