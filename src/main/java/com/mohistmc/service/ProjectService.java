package com.mohistmc.service;

import com.mohistmc.dto.response.ProjectResponseDto;
import com.mohistmc.dto.response.ProjectVersionResponseDto;
import com.mohistmc.mapper.ProjectMapper;
import com.mohistmc.mapper.ProjectVersionMapper;
import com.mohistmc.repository.ProjectRepository;
import com.mohistmc.repository.ProjectVersionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProjectService {
    private final ProjectRepository projectRepository;
    private final ProjectVersionRepository projectVersionRepository;

    private final ProjectMapper projectMapper;
    private final ProjectVersionMapper projectVersionMapper;

    public List<ProjectResponseDto> getProjects() {
        return projectRepository.findAllWithVersions().stream().map(projectMapper::toDto).toList();
    }

    public List<ProjectVersionResponseDto> getProjectVersions(String projectName) {
        return projectVersionRepository.findAllByProject_NameAndActiveIsTrueAndProject_ActiveIsTrue(projectName).stream().map(projectVersionMapper::toDto).toList();
    }
}
