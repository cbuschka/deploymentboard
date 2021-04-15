package com.github.cbuschka.deploymentboard.domain.config;

public class LdapAuthMethod extends AuthMethod
{
	public String url = "ldap://localhost:10389";
	public String bindDn = "cn=%s,ou=people,dc=example,dc=com";
	public String baseDn = "ou=people,dc=example,dc=com";
	public String userQuery = "(&(uid=%s)(objectClass=inetOrgPerson))";
}
