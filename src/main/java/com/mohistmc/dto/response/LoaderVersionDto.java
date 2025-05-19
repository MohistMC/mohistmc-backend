package com.mohistmc.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

/**
 * DTO for {@link com.mohistmc.entity.LoaderVersion}
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record LoaderVersionDto(@JsonProperty("forge_version") String forgeVersion,
                               @JsonProperty("neoforge_version") String neoforgeVersion,
                               @JsonProperty("fabric_version") String fabricVersion) implements Serializable {
}