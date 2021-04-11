package com.github.cbuschka.poboard.domain.deployment;

import com.github.cbuschka.poboard.domain.scm.CodeRepository;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class System
{
	private String name;

	private CodeRepository repository;
}
