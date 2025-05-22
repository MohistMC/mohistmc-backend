package com.mohistmc.config.properties;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Stream;

@Component
@ConfigurationProperties(prefix = "bstats")
public class BStatsProperties {
    @Value("${bstats.servers}")
    private String bStatsServers;

    public List<Integer> getServers() {
        return bStatsServers == null || bStatsServers.isEmpty() ? List.of() : Stream.of(bStatsServers.split(",")).map(String::trim)
                .map(Integer::parseInt).toList();
    }
}
