package com.mohistmc.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.time.Instant;

/**
 * DTO for {@link com.mohistmc.entity.Build}
 */
public record BuildDto(Integer id,
                       @JsonProperty("file_sha256") String fileSha256,
                       @JsonProperty("built_at") Instant builtOn,
                       @JsonProperty("commit") GitCommitDto gitInfo,
                       @JsonProperty("loader") LoaderVersionDto loaderVersion) implements Serializable {
}