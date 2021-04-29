package com.github.cbuschka.deploymentboard.domain.deployment;

import java.util.Objects;

public class SystemEnvironment
{
	private final String system;
	private final String environment;

	public SystemEnvironment(String system, String environment)
	{
		this.system = system;
		this.environment = environment;
	}

	public String getEnvironment()
	{
		return environment;
	}

	public String getSystem()
	{
		return system;
	}

	@Override
	public boolean equals(Object o)
	{
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		SystemEnvironment that = (SystemEnvironment) o;
		return system.equals(that.system) && environment.equals(that.environment);
	}

	@Override
	public int hashCode()
	{
		return Objects.hash(system, environment);
	}

	@Override
	public String toString()
	{
		return "SystemEnvironment{" +
				"system='" + system + '\'' +
				", environment='" + environment + '\'' +
				'}';
	}
}
