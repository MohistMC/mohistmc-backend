package com.mohistmc.dto.response;

import java.io.Serializable;
import java.time.Instant;

/**
 * DTO for {@link com.mohistmc.entity.GitCommit}
 */
public record GitCommitDto(String gitSha, String gitCommitMessage, String gitCommitAuthor, Instant gitCommitDate) implements Serializable {
  }