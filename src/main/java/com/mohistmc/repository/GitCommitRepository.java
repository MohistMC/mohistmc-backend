package com.mohistmc.repository;

import com.mohistmc.entity.GitCommit;
import com.mohistmc.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface GitCommitRepository extends JpaRepository<GitCommit, Integer> {
    Optional<GitCommit> findByGitShaAndProject(String gitSha, Project project);
}