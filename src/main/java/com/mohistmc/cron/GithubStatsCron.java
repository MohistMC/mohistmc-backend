package com.mohistmc.cron;

import com.mohistmc.service.stats.GithubStatsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class GithubStatsCron {
    private static final int MINUTES_30 = 30 * 60 * 1000;
    private final GithubStatsService githubStatsService;

    @Async("asyncTaskExecutor")
    @Scheduled(fixedRate = MINUTES_30)
    public void synchronizeBuilds() {
        log.info("Synchronizing github stats...");
        githubStatsService.synchronize();
        log.info("Github stats synchronization completed.");
    }
}
