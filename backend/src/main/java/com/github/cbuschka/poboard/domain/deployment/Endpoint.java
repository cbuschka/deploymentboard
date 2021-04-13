package com.github.cbuschka.poboard.domain.deployment;

import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class Endpoint
{
	private String url;

	private String command;

	private String format;
}
