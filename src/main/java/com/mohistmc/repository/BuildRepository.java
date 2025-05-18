package com.mohistmc.repository;

import com.mohistmc.entity.Build;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface BuildRepository extends JpaRepository<Build, Integer> {
    @Query("SELECT b.workflowRunId FROM Build b WHERE b.projectVersion.id = ?1")
    List<Long> findAllWorkflowRunsIds(Integer projectVersionId);

    List<Build> findAllByArtifactDownloadedIsFalse();

    List<Build> findAllByProjectVersion_ProjectNameAndProjectVersion_VersionName(String projectName, String versionName);
}