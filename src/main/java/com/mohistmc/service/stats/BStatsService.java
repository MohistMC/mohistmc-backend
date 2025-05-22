package com.mohistmc.service.stats;

import com.mohistmc.config.properties.BStatsProperties;
import com.mohistmc.dto.stats.BStatsChartResponse;
import com.mohistmc.dto.stats.BStatsDto;
import com.mohistmc.dto.stats.BStatsResponse;
import com.mohistmc.enums.BStatsTypeEnum;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Getter
public class BStatsService {
    public static final int BSTATS_MAX_ELEMENTS = 17520;
    public static final int BSTATS_MIN_ELEMENTS = 1;

    private final BStatsProperties bStatsProperties;

    private final RestTemplate restTemplate = new RestTemplate();
    private BStatsResponse bStats = new BStatsResponse(new BStatsDto(0, 0), new BStatsDto(0, 0));

    public void synchronize() {
        bStats = getBStats();
    }

    public BStatsResponse getBStats() {
        List<Integer> servers = bStatsProperties.getServers();

        int maxServers = servers.stream().mapToInt(id -> getFilteredBStats(id, BStatsTypeEnum.SERVERS, BSTATS_MAX_ELEMENTS)).sum();
        int currentServers = servers.stream().mapToInt(id -> getFilteredBStats(id, BStatsTypeEnum.SERVERS, BSTATS_MIN_ELEMENTS)).sum();
        int maxPlayers = servers.stream().mapToInt(id -> getFilteredBStats(id, BStatsTypeEnum.PLAYERS, BSTATS_MAX_ELEMENTS)).sum();
        int currentPlayers = servers.stream().mapToInt(id -> getFilteredBStats(id, BStatsTypeEnum.PLAYERS, BSTATS_MIN_ELEMENTS)).sum();

        return new BStatsResponse(
                new BStatsDto(maxServers, currentServers),
                new BStatsDto(maxPlayers, currentPlayers)
        );
    }

    private int getFilteredBStats(int pluginId, BStatsTypeEnum bstatsType, int maxElements) {
        try {
            ResponseEntity<List<List<Object>>> response = restTemplate.exchange(
                    "https://bstats.org/api/v1/plugins/" + pluginId + "/charts/" + bstatsType.getType() + "/data?maxElements=" + maxElements,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<>() {
                    }
            );

            return response.getBody() == null ? 0 :
                    response.getBody().stream()
                            .map(entry -> new BStatsChartResponse(
                                    ((Number) entry.getFirst()).longValue(),
                                    ((Number) entry.get(1)).intValue()
                            ))
                            .mapToInt(BStatsChartResponse::value)
                            .max()
                            .orElse(0);
        } catch (Exception e) {
            System.err.println("Error fetching bStats data: " + e.getMessage());
            return 0;
        }
    }
}
