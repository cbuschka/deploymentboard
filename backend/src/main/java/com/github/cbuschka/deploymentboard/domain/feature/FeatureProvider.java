package com.github.cbuschka.deploymentboard.domain.feature;

import com.github.cbuschka.deploymentboard.domain.config.ConfigProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class FeatureProvider
{
	@Autowired
	private ConfigProvider configProvider;

	public Map<Feature, FeatureState> getFeatures()
	{
		return Arrays.stream(Feature.values())
				.collect(Collectors.toMap((f) -> f,
						(f) -> new FeatureState(isEnabled(f)),
						(a, b) -> new FeatureState(a.enabled && b.enabled)));
	}

	public boolean isEnabled(Feature feature)
	{
		return this.configProvider
				.getConfig()
				.features
				.stream()
				.filter((fc) -> fc.name == feature)
				.map((fc) -> fc.enabled)
				.findFirst()
				.orElse(Boolean.FALSE);
	}
}
