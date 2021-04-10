package com.github.cbuschka.poboard.web.api;

import java.util.LinkedHashMap;
import java.util.Map;

public class DashboardStateResponse
{
	public final Map<String, Map<String, SystemEnvironment>> environments = new LinkedHashMap<>();

	public static DashboardStateResponse newWithEnvironments(String... envs)
	{
		DashboardStateResponse response = new DashboardStateResponse();
		for (String env : envs)
		{
			response.environments.put(env, new LinkedHashMap<>());
		}
		return response;
	}

	public DashboardStateResponse withSystemEnvironment(String env, String system, SystemEnvironment systemEnvironment)
	{
		this.environments.get(env).put(system, systemEnvironment);
		return this;
	}

	public static class SystemEnvironment
	{
		public String version;

		public SystemEnvironment(String version)
		{
			this.version = version;
		}
	}
}
