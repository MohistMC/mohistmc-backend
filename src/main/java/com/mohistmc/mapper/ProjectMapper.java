package com.mohistmc.mapper;

import com.mohistmc.dto.response.ProjectDto;
import com.mohistmc.dto.response.ProjectVersionDto;
import com.mohistmc.entity.Project;
import com.mohistmc.entity.ProjectVersion;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface ProjectMapper {
    @Mapping(source = "projectVersions", target = "versions")
    ProjectDto toDto(Project project);

    @Mapping(source = "versionName", target = "name")
    ProjectVersionDto toProjectVersionDto(ProjectVersion projectVersion);
}