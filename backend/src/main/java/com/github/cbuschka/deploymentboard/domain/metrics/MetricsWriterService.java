package com.github.cbuschka.deploymentboard.domain.metrics;

import com.github.cbuschka.deploymentboard.util.CacheStats;
import com.github.cbuschka.deploymentboard.util.CacheStatsProvider;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@AllArgsConstructor
public class MetricsWriterService {

	private final List<CacheStatsProvider> cacheStatsProviders;

	@Scheduled(cron = "0 */5 * * * *")
	public void writeMetrics() {
		for (CacheStatsProvider provider : cacheStatsProviders) {
			CacheStats cacheStats = provider.getCacheStats();
			log.info("Cache {} has {} entries.", cacheStats.getCacheName(), cacheStats.getEntries());
		}
	}
}
