package com.mohistmc.cron;

import com.mohistmc.service.stats.BStatsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class BStatsCron {
    private static final int MINUTES_30 = 30 * 60 * 1000;
    private final BStatsService bStatsService;

    private boolean isSynchronizing = false;

    @Async("asyncTaskExecutor")
    @Scheduled(fixedRate = MINUTES_30)
    public void synchronizeBuilds() {
        if (isSynchronizing) {
            log.info("Bstats synchronization is already in progress.");
            return;
        }
        isSynchronizing = true;
        log.info("Synchronizing bstats...");
        bStatsService.synchronize();
        log.info("Bstats synchronization completed.");
        isSynchronizing = false;
    }
}
