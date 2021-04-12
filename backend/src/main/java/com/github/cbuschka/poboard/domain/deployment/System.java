package com.github.cbuschka.poboard.domain.deployment;

import com.github.cbuschka.poboard.domain.scm.CodeRepository;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Map;
import java.util.Optional;

@NoArgsConstructor
@Getter
public class System
{
	private String name;

	private CodeRepository repository;

	private Map<String, Endpoint> endpoints;

	public Optional<Endpoint> getEndpoint(String env)
	{
		if (this.endpoints == null)
		{
			return Optional.empty();
		}

		return Optional.ofNullable(endpoints.get(env));
	}
}
