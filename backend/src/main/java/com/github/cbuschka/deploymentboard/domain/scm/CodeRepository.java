package com.github.cbuschka.deploymentboard.domain.scm;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CodeRepository
{
	private String url;

	private Integer connectTimeoutMillis;
}
