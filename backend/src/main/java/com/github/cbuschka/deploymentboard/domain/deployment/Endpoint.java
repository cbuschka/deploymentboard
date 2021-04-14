package com.github.cbuschka.deploymentboard.domain.deployment;

import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class Endpoint
{
	private String url;

	private String command;

	private String format;

	private Integer recheckTimeoutMillis;

	private Integer connectTimeoutMillis;

	private Integer readTimeoutMillis;
}
