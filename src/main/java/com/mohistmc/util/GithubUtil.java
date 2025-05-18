package com.mohistmc.util;

import com.mohistmc.dto.WorkflowRunWithArtifacts;
import com.mohistmc.entity.Project;
import lombok.extern.slf4j.Slf4j;
import org.kohsuke.github.*;
import org.kohsuke.github.function.InputStreamFunction;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

@Slf4j
public class GithubUtil {
    public static void downloadArtifact(GHArtifact artifact, Path filePath) throws IOException {
        Files.createDirectories(filePath.getParent());
        if (Files.exists(filePath)) {
            Files.delete(filePath);
        } else {
            Files.createFile(filePath);
        }

        artifact.download((InputStreamFunction<Void>) inputStream -> {
            log.info("Downloading artifact {} to {}", artifact.getId(), filePath);
            try (FileOutputStream fos = new FileOutputStream(filePath.toFile())) {
                byte[] buffer = new byte[8192];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    fos.write(buffer, 0, bytesRead);
                }
            }
            return null;
        });
    }

    public static WorkflowRunWithArtifacts getWorkflowRunWithArtifacts(GHWorkflowRun workflowRun) {
        try {
            return new WorkflowRunWithArtifacts(workflowRun, workflowRun.listArtifacts().toList());
        } catch (IOException e) {
            log.error("Error listing artifacts for workflow run {}: {}", workflowRun.getId(), e.getMessage());
            return new WorkflowRunWithArtifacts(workflowRun, List.of());
        }
    }

    public static List<GHWorkflowRun> ghWorkflowIterableToList(PagedIterable<GHWorkflowRun> workflowRuns) {
        List<GHWorkflowRun> allWorkflowRuns;
        try {
            allWorkflowRuns = workflowRuns.toList();
        } catch (IOException e) {
            return null;
        }
        return allWorkflowRuns;
    }

    public static GHRepository getGhRepositoryForProject(GitHub githubConnection, Project project) throws IOException {
        GHOrganization organization = githubConnection.getOrganization(project.getRepositoryOwner());
        if (organization == null) {
            log.error("Organization {} not found", project.getRepositoryOwner());
            return null;
        }

        GHRepository repo = organization.getRepository(project.getRepositoryName());
        if (repo == null) {
            log.error("Repository {} not found", project.getRepositoryName());
            return null;
        }
        return repo;
    }
}
