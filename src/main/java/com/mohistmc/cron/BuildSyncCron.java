package com.mohistmc.cron;

import com.mohistmc.service.util.GitHubArtifactService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class BuildSyncCron {
    private static final int TEN_MINUTES = 10 * 60 * 1000;
    private final GitHubArtifactService githubArtifactService;

    private boolean isSynchronizing = false;

    @Async("asyncTaskExecutor")
    @Scheduled(fixedRate = TEN_MINUTES)
    public void synchronizeBuilds() {
        if (isSynchronizing) {
            log.info("Build synchronization is already in progress.");
            return;
        }
        isSynchronizing = true;
        log.info("Synchronizing builds...");
        githubArtifactService.synchronize();
        log.info("Build synchronization completed.");
        isSynchronizing = false;
    }
}
