package com.github.cbuschka.deploymentboard.domain.config;

import java.util.ArrayList;
import java.util.List;

public class SimpleAuthMethod extends AuthMethod
{
	public List<User> users = new ArrayList<>();
}
