package com.github.cbuschka.poboard.domain.config;

import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class Settings
{
	private Integer connectTimeoutMillis;
	private Integer readTimeoutMillis;
}
