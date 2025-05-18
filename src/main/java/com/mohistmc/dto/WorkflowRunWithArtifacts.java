package com.mohistmc.dto;

import org.kohsuke.github.GHArtifact;
import org.kohsuke.github.GHWorkflowRun;

import java.util.List;

public record WorkflowRunWithArtifacts(GHWorkflowRun workflowRun, List<GHArtifact> artifacts) {
}
