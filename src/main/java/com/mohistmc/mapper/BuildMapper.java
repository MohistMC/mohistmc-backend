package com.mohistmc.mapper;

import com.mohistmc.dto.response.BuildResponseDto;
import com.mohistmc.entity.Build;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface BuildMapper {
    BuildResponseDto toDto(Build build);
}