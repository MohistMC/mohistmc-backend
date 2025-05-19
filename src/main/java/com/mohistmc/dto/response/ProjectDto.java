package com.mohistmc.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.Set;

/**
 * DTO for {@link com.mohistmc.entity.Project}
 */
public record ProjectDto(String name,
                         @JsonProperty("versions") Set<ProjectVersionDto> projectVersions) implements Serializable {
}