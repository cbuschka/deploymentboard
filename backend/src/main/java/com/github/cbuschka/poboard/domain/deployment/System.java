package com.github.cbuschka.poboard.domain.deployment;

import com.github.cbuschka.poboard.domain.scm.CodeRepository;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Map;

@NoArgsConstructor
@Getter
public class System
{
	private String name;

	private CodeRepository repository;

	private Map<String, Endpoint> endpoints;
}
