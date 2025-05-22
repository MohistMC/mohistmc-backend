package com.mohistmc.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.time.Instant;

/**
 * DTO for {@link com.mohistmc.entity.Build}
 */
public record BuildResponseDto(Integer id,
                               @JsonProperty("file_sha256") String fileSha256,
                               @JsonProperty("build_date") Instant builtOn,
                               @JsonProperty("commit") GitCommitResponseDto gitInfo,
                               @JsonProperty("loader") LoaderVersionResponseDto loaderVersion) implements Serializable {
}