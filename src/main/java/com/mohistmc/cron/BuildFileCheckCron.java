package com.mohistmc.cron;

import com.mohistmc.entity.Build;
import com.mohistmc.repository.BuildRepository;
import com.mohistmc.service.BuildStorageService;
import com.mohistmc.service.BuildStorageService.FileState;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Checks that every build marked as downloaded still has its object in the bucket.
 * A build without it is marked as not downloaded, so that the build synchronization downloads
 * it again from GitHub; one found builds.max-missing-file-count times without it is deactivated.
 */
@Slf4j
@Component
public class BuildFileCheckCron extends AbstractSyncCron {
    // above this many missing files, a run changes nothing if they are also most of the builds
    private static final int MASS_LOSS_THRESHOLD = 10;

    private final BuildRepository buildRepository;
    private final BuildStorageService buildStorageService;
    private final int maxMissingFileCount;

    public BuildFileCheckCron(BuildRepository buildRepository, BuildStorageService buildStorageService,
                              @Value("${builds.max-missing-file-count:3}") int maxMissingFileCount) {
        this.buildRepository = buildRepository;
        this.buildStorageService = buildStorageService;
        this.maxMissingFileCount = maxMissingFileCount;
    }

    @Async("asyncTaskExecutor")
    @Scheduled(initialDelayString = "${builds.file-check.initial-delay:PT5M}", fixedDelayString = "${builds.file-check.interval:PT6H}")
    public void checkBuildFiles() {
        runOnce();
    }

    @Override
    protected String taskName() {
        return "Build file check";
    }

    @Override
    protected void doSynchronize() {
        List<Build> builds = buildRepository.findAllByArtifactDownloadedIsTrueAndActiveIsTrueOrderById();
        List<Integer> missing = new ArrayList<>();
        int unknown = 0;
        for (Build build : builds) {
            switch (buildStorageService.checkFile(build)) {
                case MISSING -> missing.add(build.getId());
                case UNKNOWN -> unknown++;
                case PRESENT -> { }
            }
        }
        log.info("{} builds checked: {} without their file, {} the storage did not answer for",
                builds.size(), missing.size(), unknown);

        // a wrong bucket or prefix makes every file look missing: that is not a loss
        if (missing.size() > MASS_LOSS_THRESHOLD && missing.size() * 2 > builds.size()) {
            log.error("{} of {} builds look like they lost their file: the storage is likely unavailable, nothing changed",
                    missing.size(), builds.size());
            return;
        }

        missing.forEach(this::markMissing);
    }

    private void markMissing(Integer id) {
        // read again: the build may have been downloaded again since the check
        Build build = buildRepository.findWithDetailsById(id).orElse(null);
        if (build == null || !Boolean.TRUE.equals(build.getArtifactDownloaded())
                || buildStorageService.checkFile(build) != FileState.MISSING) {
            return;
        }

        int count = build.getMissingFileCount() + 1;
        build.setMissingFileCount(count);
        build.setArtifactDownloaded(false);
        build.setFileKey(null);
        if (count >= maxMissingFileCount) {
            build.setActive(false);
            log.error("Build {} lost its file {} times: deactivated", id, count);
        } else {
            log.warn("Build {} lost its file ({} of {} times): it will be downloaded again", id, count, maxMissingFileCount);
        }
        buildRepository.save(build);
    }
}
