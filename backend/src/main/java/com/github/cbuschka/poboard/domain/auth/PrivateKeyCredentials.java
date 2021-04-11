package com.github.cbuschka.poboard.domain.auth;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PrivateKeyCredentials extends Credentials
{
	private String data;
}
