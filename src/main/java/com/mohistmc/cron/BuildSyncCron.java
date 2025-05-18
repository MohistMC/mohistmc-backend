package com.mohistmc.cron;

import com.mohistmc.service.GitHubArtifactService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class BuildSyncCron {
    private final GitHubArtifactService githubArtifactService;

    private static final int TEN_MINUTES = 10 * 60 * 1000;

    @Scheduled(fixedRate = TEN_MINUTES)
    public void synchronizeBuilds() {
        log.info("Synchronizing builds...");
        githubArtifactService.synchronize();
        log.info("Build synchronization completed.");
    }
}
