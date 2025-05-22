package com.mohistmc.dto.response;

import com.mohistmc.dto.stats.BStatsResponse;
import com.mohistmc.dto.stats.GithubStats;

public record StatsResponseDto(BStatsResponse bstats, GithubStats github, long downloads) {
}
