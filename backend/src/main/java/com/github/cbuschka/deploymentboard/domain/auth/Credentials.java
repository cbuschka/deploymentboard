package com.github.cbuschka.deploymentboard.domain.auth;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Getter;

import java.util.Set;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
@JsonSubTypes({
		@JsonSubTypes.Type(name = "password", value = PasswordCredentials.class),
		@JsonSubTypes.Type(name = "privateKey", value = PrivateKeyCredentials.class),
		@JsonSubTypes.Type(name = "authToken", value = AuthTokenCredentials.class)
})
@Getter
public abstract class Credentials
{
	private Set<String> allowedHostnames;
	private Set<String> allowedUsernames;
}
