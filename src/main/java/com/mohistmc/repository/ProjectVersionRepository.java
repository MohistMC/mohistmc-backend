package com.mohistmc.repository;

import com.mohistmc.entity.ProjectVersion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProjectVersionRepository extends JpaRepository<ProjectVersion, Integer> {
    List<ProjectVersion> findAllByProject_NameAndActiveIsTrueAndProject_ActiveIsTrue(String projectName);
}