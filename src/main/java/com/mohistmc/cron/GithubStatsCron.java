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

    private boolean isSynchronizing = false;

    @Async("asyncTaskExecutor")
    @Scheduled(fixedRate = MINUTES_30)
    public void synchronizeBuilds() {
        if (isSynchronizing) {
            log.info("Github stats synchronization is already in progress.");
            return;
        }
        isSynchronizing = true;
        log.info("Synchronizing github stats...");
        githubStatsService.synchronize();
        log.info("Github stats synchronization completed.");
        isSynchronizing = false;
    }
}
