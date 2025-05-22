package com.mohistmc.mapper;

import com.mohistmc.dto.response.ProjectVersionResponseDto;
import com.mohistmc.entity.ProjectVersion;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface ProjectVersionMapper {
    ProjectVersionResponseDto toDto(ProjectVersion projectVersion);
}