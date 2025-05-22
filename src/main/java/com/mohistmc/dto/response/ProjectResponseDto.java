package com.mohistmc.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.Set;

/**
 * DTO for {@link com.mohistmc.entity.Project}
 */
public record ProjectResponseDto(String name,
                                 @JsonProperty("versions") Set<ProjectVersionResponseDto> projectVersions) implements Serializable {
}