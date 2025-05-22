package com.mohistmc.controller;

import com.mohistmc.dto.response.ProjectResponseDto;
import com.mohistmc.service.ProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/project")
@RequiredArgsConstructor
public class ProjectController {
    private final ProjectService projectService;

    @GetMapping("/list")
    public List<ProjectResponseDto> getProjects() {
        return projectService.getProjects();
    }
}
