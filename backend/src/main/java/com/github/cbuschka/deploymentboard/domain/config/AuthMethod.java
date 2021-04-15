package com.github.cbuschka.deploymentboard.domain.config;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
@JsonSubTypes({
		@JsonSubTypes.Type(name = "simple", value = SimpleAuthMethod.class),
		@JsonSubTypes.Type(name = "ldap", value = LdapAuthMethod.class)
})
public abstract class AuthMethod
{
	public String type;
}
