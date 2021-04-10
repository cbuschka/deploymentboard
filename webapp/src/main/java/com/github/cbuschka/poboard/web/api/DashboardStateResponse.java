package com.github.cbuschka.poboard.web.api;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class DashboardStateResponse
{
	public final Map<String, List<Environment>> environments = new LinkedHashMap<>();

	public static DashboardStateResponse newWithProducts(String... products)
	{
		DashboardStateResponse response = new DashboardStateResponse();
		for (String product : products)
		{
			response.environments.put(product, new ArrayList<>());
		}
		return response;
	}

	public DashboardStateResponse withEnvironment(String product, Environment environment)
	{
		this.environments.get(product).add(environment);
		return this;
	}

	public static class Environment
	{
		public String name;
		public String version;

		public Environment(String name, String version)
		{
			this.name = name;
			this.version = version;
		}
	}
}
