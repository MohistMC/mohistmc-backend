package com.mohistmc.dto.response;

import java.io.Serializable;
import java.util.Set;

/**
 * DTO for {@link com.mohistmc.entity.Project}
 */
public record ProjectDto(String name, Set<ProjectVersionDto> versions) implements Serializable {
  }