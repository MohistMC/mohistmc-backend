package com.mohistmc.cron;

import com.mohistmc.config.properties.S3Properties;
import com.mohistmc.entity.Build;
import com.mohistmc.repository.BuildRepository;
import com.mohistmc.service.BuildStorageService;
import com.mohistmc.util.FileUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Moves the builds still in builds.path to the S3 bucket, one at a time, when s3.migrate-local-builds is set.
 * A file is only copied when it matches the sha256 recorded for its build, and only deleted, with
 * s3.delete-migrated-files, once the bucket holds it.
 */
@Slf4j
@RequiredArgsConstructor
@Component
public class BuildStorageMigrationCron extends AbstractSyncCron {
    private static final int TEN_MINUTES = 10 * 60 * 1000;

    private final BuildRepository buildRepository;
    private final BuildStorageService buildStorageService;
    private final S3Properties s3Properties;

    @Async("asyncTaskExecutor")
    @Scheduled(initialDelay = 60 * 1000, fixedDelay = TEN_MINUTES)
    public void migrateBuilds() {
        if (buildStorageService.usesS3() && s3Properties.isMigrateLocalBuilds()) {
            runOnce();
        }
    }

    @Override
    protected String taskName() {
        return "Build storage migration";
    }

    @Override
    protected void doSynchronize() {
        int migrated = 0;
        for (Build build : buildRepository.findAllByArtifactDownloadedIsTrueAndFileKeyIsNullOrderById()) {
            if (migrate(build)) migrated++;
        }
        log.info("{} builds moved to the bucket", migrated);
    }

    private boolean migrate(Build build) {
        Path file = buildStorageService.getLocalPath(build);
        try {
            if (!Files.isRegularFile(file)) {
                log.warn("Build {} has no file at {}, left as is", build.getId(), file);
                return false;
            }

            String sha256 = FileUtil.getFileSha256(file);
            if (build.getFileSha256() != null && !build.getFileSha256().equals(sha256)) {
                log.error("Build {} does not match its recorded sha256, left as is", build.getId());
                return false;
            }
            build.setFileSha256(sha256);

            buildStorageService.migrateToS3(build);
            buildRepository.save(build);

            if (s3Properties.isDeleteMigratedFiles()) {
                Files.delete(file);
            }
            return true;
        } catch (IOException e) {
            log.error("Could not move build {} to the bucket: {}", build.getId(), e.getMessage(), e);
            return false;
        }
    }
}
