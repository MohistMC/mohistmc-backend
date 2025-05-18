package com.mohistmc.service;

import com.mohistmc.dto.response.ProjectDto;
import com.mohistmc.dto.response.ProjectVersionDto;
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

    public List<ProjectDto> getProjects() {
        return projectRepository.findAllWithVersions().stream().map(projectMapper::toDto).toList();
    }

    public List<ProjectVersionDto> getProjectVersions(String projectName) {
        return projectVersionRepository.findAllByProject_Name(projectName).stream().map(projectVersionMapper::toDto).toList();
    }
}
