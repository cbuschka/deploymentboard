package com.github.cbuschka.deploymentboard.domain.auth;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class AuthTokenCredentials extends Credentials
{
	private String value;
}
