package com.github.cbuschka.deploymentboard.web.api;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class LoginResponse
{
	public final boolean ok = true;

	public final String username;
}
