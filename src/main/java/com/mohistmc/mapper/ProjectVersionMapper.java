package com.mohistmc.mapper;

import com.mohistmc.dto.response.ProjectVersionDto;
import com.mohistmc.entity.ProjectVersion;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface ProjectVersionMapper {
    @Mapping(source = "versionName", target = "name")
    ProjectVersionDto toDto(ProjectVersion projectVersion);
}