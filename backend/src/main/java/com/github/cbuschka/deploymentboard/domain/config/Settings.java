package com.github.cbuschka.deploymentboard.domain.config;

import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class Settings
{
	private Integer reloadTimeoutMillis;
	private Integer connectTimeoutMillis;
	private Integer readTimeoutMillis;
	private Integer recheckTimeoutMillis;
}
