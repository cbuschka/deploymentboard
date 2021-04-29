package com.github.cbuschka.deploymentboard.domain.deployment;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@NoArgsConstructor
@Getter
@Setter
public class Endpoint
{
	private String url;

	private String command;

	private String format;

	private Integer recheckTimeoutMillis;

	private Integer connectTimeoutMillis;

	private Integer readTimeoutMillis;

	private Set<String> versionAliases;

	private Set<String> branchAliases;

	private Set<String> commitishAliases;

	private Set<String> buildTimestampAliases;

	@Override
	public String toString()
	{
		return "Endpoint{url=" + url + "}";
	}
}
