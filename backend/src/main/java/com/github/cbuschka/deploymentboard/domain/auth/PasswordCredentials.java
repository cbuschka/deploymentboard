package com.github.cbuschka.deploymentboard.domain.auth;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PasswordCredentials extends Credentials
{
	private String password;
}
