package com.github.cbuschka.deploymentboard.util;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class CacheStats {

	private final String cacheName;

	private final int entries;
}
