package com.mohistmc.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.time.Instant;

/**
 * DTO for {@link com.mohistmc.entity.GitCommit}
 */
public record GitCommitResponseDto(@JsonProperty("hash") String gitSha,
                                   @JsonProperty("changelog") String gitCommitMessage,
                                   @JsonProperty("author") String gitCommitAuthor,
                                   @JsonProperty("commit_date") Instant gitCommitDate) implements Serializable {
}