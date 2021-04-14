package com.github.cbuschka.deploymentboard.domain.config;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Arrays;
import java.util.List;

@NoArgsConstructor
@Getter
public class Settings
{
	private Integer reloadTimeoutMillis;
	private Integer connectTimeoutMillis;
	private Integer readTimeoutMillis;
	private Integer recheckTimeoutMillis;
	private List<String> buildTimestampAliases;
	private List<String> versionAliases;
	private List<String> commitishAliases;
	private List<String> branchAliases;
}
