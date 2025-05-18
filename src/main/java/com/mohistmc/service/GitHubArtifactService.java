package com.mohistmc.service;

import com.mohistmc.config.properties.FolderProperties;
import com.mohistmc.config.properties.GithubProperties;
import com.mohistmc.dto.WorkflowRunWithArtifacts;
import com.mohistmc.entity.Build;
import com.mohistmc.entity.GitCommit;
import com.mohistmc.entity.Project;
import com.mohistmc.entity.ProjectVersion;
import com.mohistmc.enums.GitPlatformEnum;
import com.mohistmc.repository.BuildRepository;
import com.mohistmc.repository.GitCommitRepository;
import com.mohistmc.repository.ProjectRepository;
import com.mohistmc.util.FileUtil;
import com.mohistmc.util.GithubUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.kohsuke.github.*;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Path;
import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class GitHubArtifactService {
    private final ProjectRepository projectRepository;
    private final BuildRepository buildRepository;
    private final GitCommitRepository gitCommitRepository;

    private final GithubProperties githubProperties;
    private final FolderProperties folderProperties;

    public void synchronize() {
        GitHub githubConnection = githubProperties.connect();

        projectRepository.findAllWithVersions().stream()
                .filter(project -> project.getGitPlatform() == GitPlatformEnum.GITHUB)
                .forEach(project -> {
                    try {
                        synchronizeGithubProject(githubConnection, project);
                    } catch (IOException e) {
                        log.error("Error synchronizing project {}: {}", project.getName(), e.getMessage());
                    }
                });
    }

    private void synchronizeGithubProject(GitHub githubConnection, Project project) throws IOException {
        GHRepository repo = GithubUtil.getGhRepositoryForProject(githubConnection, project);
        if (repo == null) return;

        log.info("Downloading previously failed artifacts downloads for project: {}", project.getName());
        downloadPreviouslyFailedDownloads(repo);

        log.info("Synchronizing project: {}", project.getName());
        project.getProjectVersions().forEach(version -> saveBuildsForVersion(project, version, repo));
    }

    private void saveBuildsForVersion(Project project, ProjectVersion version, GHRepository repo) {
        List<GHWorkflowRun> unvisitedWorkflowRuns = getGhWorkflowRuns(project, version, repo);
        if (unvisitedWorkflowRuns == null) return;

        log.info("Fetched workflow runs for version {} in project {}", version.getVersionName(), project.getName());
        List<Build> builds = ghWorkflowsArtifactToBuilds(version, unvisitedWorkflowRuns);

        if (builds.isEmpty()) {
            log.warn("No builds found for version {} in project {}", version.getVersionName(), project.getName());
            return;
        }

        log.info("Found {} builds to synchronize for version {} in project {}", builds.size(), version.getVersionName(), project.getName());
        builds.forEach(this::downloadArtifactAndSaveHash);
    }

    private void downloadPreviouslyFailedDownloads(GHRepository repository) {
        List<Build> builds = buildRepository.findAllByArtifactDownloadedIsFalse();

        for (Build build : builds) {
            try {
                GHArtifact artifact = repository.getArtifact(build.getArtifactId());
                if (artifact == null) {
                    log.warn("Artifact {} not found for build {}", build.getArtifactId(), build.getId());
                    builds.remove(build);
                    continue;
                }
                build.setGhArtifact(artifact);
            } catch (IOException ignored) {
                builds.remove(build);
                buildRepository.delete(build);
            }
        }
        builds.forEach(this::downloadArtifactAndSaveHash);
    }

    private void downloadArtifactAndSaveHash(Build build) {
        buildRepository.save(build);

        try {
            Path buildPath = folderProperties.getBuildPath(build);
            GithubUtil.downloadArtifact(build.getGhArtifact(), buildPath);
            String sha256 = FileUtil.getFileSha256(buildPath);
            build.setFileSha256(sha256);
            build.setArtifactDownloaded(true);
        } catch (IOException e) {
            log.error("Error downloading artifact {}: {}", build.getGhArtifact(), e.getMessage(), e);
        }

        log.info("Saving build {} (id {})", build.getGitInfo().getGitSha(), build.getId());
        buildRepository.save(build);
    }

    private List<GHWorkflowRun> getGhWorkflowRuns(Project project, ProjectVersion version, GHRepository repo) {
        GHWorkflowRunQueryBuilder workflowRunsQueryBuilder = repo.queryWorkflowRuns().branch(version.getGitBranch());
        PagedIterable<GHWorkflowRun> workflowRuns = workflowRunsQueryBuilder.list();

        List<GHWorkflowRun> allWorkflowRuns = GithubUtil.ghWorkflowIterableToList(workflowRuns);
        if (allWorkflowRuns == null || allWorkflowRuns.isEmpty()) {
            log.warn("No workflow runs found for version {} in project {}", version.getVersionName(), project.getName());
            return null;
        }

        List<Long> visitedWorkflowRunIds = buildRepository.findAllWorkflowRunsIds(version.getId());
        return allWorkflowRuns.stream()
                .filter(workflowRun -> !visitedWorkflowRunIds.contains(workflowRun.getId()))
                .collect(Collectors.toList());
    }

    private List<Build> ghWorkflowsArtifactToBuilds(ProjectVersion version, List<GHWorkflowRun> allWorkflowRuns) {
        return allWorkflowRuns.stream()
                .map(GithubUtil::getWorkflowRunWithArtifacts)
                .filter(workflowRunWithArtifacts -> workflowRunWithArtifacts.artifacts().size() == 1
                        && !workflowRunWithArtifacts.artifacts().getFirst().isExpired())
                .map(workflowRunWithArtifacts -> mapWorkflowToBuild(version, workflowRunWithArtifacts))
                .toList();
    }

    private Build mapWorkflowToBuild(ProjectVersion version, WorkflowRunWithArtifacts workflowRunWithArtifacts) {
        GHArtifact artifact = workflowRunWithArtifacts.artifacts().getFirst();

        GitCommit gitCommit = gitCommitRepository
                .findByGitShaAndProject(workflowRunWithArtifacts.workflowRun().getHeadSha(), version.getProject())
                .orElseGet(() -> new GitCommit()
                        .setGitSha(workflowRunWithArtifacts.workflowRun().getHeadSha())
                        .setGitCommitMessage(workflowRunWithArtifacts.workflowRun().getHeadCommit().getMessage())
                        .setGitCommitAuthor(workflowRunWithArtifacts.workflowRun().getHeadCommit().getAuthor().getName())
                        .setGitCommitEmail(workflowRunWithArtifacts.workflowRun().getHeadCommit().getAuthor().getEmail())
                        .setGitCommitDate(workflowRunWithArtifacts.workflowRun().getHeadCommit().getTimestamp())
                        .setProject(version.getProject())
                );
        gitCommitRepository.save(gitCommit);

        Build build = new Build().setProjectVersion(version)
                .setGitInfo(gitCommit)
                .setWorkflowRunId(workflowRunWithArtifacts.workflowRun().getId())
                .setArtifactId(artifact.getId())
                .setGhArtifact(artifact);

        try {
            build.setBuiltOn(artifact.getCreatedAt());
        } catch (IOException e) {
            build.setBuiltOn(Instant.now());
        }

        return build;
    }

}
