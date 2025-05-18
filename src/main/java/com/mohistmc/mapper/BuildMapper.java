package com.mohistmc.mapper;

import com.mohistmc.dto.response.BuildDto;
import com.mohistmc.entity.Build;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface BuildMapper {
    BuildDto toDto(Build build);
}