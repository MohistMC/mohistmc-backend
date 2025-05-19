package com.mohistmc.mapper;

import com.mohistmc.dto.response.ProjectDto;
import com.mohistmc.entity.Project;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface ProjectMapper {
    ProjectDto toDto(Project project);
}