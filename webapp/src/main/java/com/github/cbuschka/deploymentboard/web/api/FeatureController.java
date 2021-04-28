package com.github.cbuschka.deploymentboard.web.api;

import com.github.cbuschka.deploymentboard.domain.feature.Feature;
import com.github.cbuschka.deploymentboard.domain.feature.FeatureProvider;
import com.github.cbuschka.deploymentboard.domain.feature.FeatureState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class FeatureController
{
	@Autowired
	private FeatureProvider featureProvider;

	@GetMapping(value = "/api/features")
	public @ResponseBody
	ResponseEntity<Map<Feature, FeatureState>> getFeatures()
	{
		return ResponseEntity.ok(this.featureProvider.getFeatures());
	}
}
