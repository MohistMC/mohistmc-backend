package com.mohistmc.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

/**
 * DTO for {@link com.mohistmc.entity.ProjectVersion}
 */
public record ProjectVersionResponseDto(
        @JsonProperty("name") String versionName) implements Serializable {
}