package com.github.cbuschka.deploymentboard.domain.deployment;

import com.github.cbuschka.deploymentboard.domain.scm.CodeRepository;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;
import java.util.Optional;

@NoArgsConstructor
@Getter
@Setter
public class System
{
	private String name;

	private CodeRepository repository;

	private Map<String, Endpoint> endpoints;

	private String mainBranch = "origin/main";

	public String getMainBranch()
	{
		return this.mainBranch;
	}

	public Optional<Endpoint> getEndpoint(String env)
	{
		if (this.endpoints == null)
		{
			return Optional.empty();
		}

		return Optional.ofNullable(endpoints.get(env));
	}
}
