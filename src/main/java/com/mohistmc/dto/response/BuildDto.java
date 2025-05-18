package com.mohistmc.dto.response;

import java.io.Serializable;
import java.time.Instant;

/**
 * DTO for {@link com.mohistmc.entity.Build}
 */
public record BuildDto(Integer id, String fileSha256, Instant builtOn, GitCommitDto gitInfo) implements Serializable {
}