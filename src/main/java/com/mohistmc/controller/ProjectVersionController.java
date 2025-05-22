package com.mohistmc.controller;

import com.mohistmc.dto.response.ProjectVersionResponseDto;
import com.mohistmc.service.ProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/project/{projectName}")
@RequiredArgsConstructor
public class ProjectVersionController {
    private final ProjectService projectService;

    @GetMapping("/versions")
    public List<ProjectVersionResponseDto> getProjectVersions(@PathVariable String projectName) {
        return projectService.getProjectVersions(projectName);
    }
}

