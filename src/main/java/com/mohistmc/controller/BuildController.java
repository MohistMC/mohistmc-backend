package com.mohistmc.controller;

import com.mohistmc.dto.response.BuildDto;
import com.mohistmc.entity.Build;
import com.mohistmc.mapper.BuildMapper;
import com.mohistmc.service.BuildService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/project/{projectName}/{versionName}")
@RequiredArgsConstructor
public class BuildController {
    private final BuildService buildService;
    private final BuildMapper buildMapper;

    @GetMapping("/builds")
    public List<BuildDto> getProjectVersions(@PathVariable String projectName,
                                             @PathVariable String versionName) {
        return buildService.getBuildsByProjectAndVersion(projectName, versionName)
                .stream()
                .map(buildMapper::toDto)
                .toList();
    }

    @GetMapping("/builds/{buildId}")
    public BuildDto getBuild(@PathVariable String projectName,
                             @PathVariable String versionName,
                             @PathVariable Integer buildId) {
        return buildMapper.toDto(buildService.getBuildById(buildId));
    }

    @GetMapping("/builds/latest")
    public BuildDto getLatestBuild(@PathVariable String projectName,
                                   @PathVariable String versionName) {
        return buildMapper.toDto(buildService.getLatestBuild(projectName, versionName));
    }

    @GetMapping("/builds/{buildId}/download")
    public ResponseEntity<Resource> downloadBuild(@PathVariable String projectName,
                                                  @PathVariable String versionName,
                                                  @PathVariable Integer buildId) {
        Build build = buildService.getBuildById(buildId);
        Resource buildFile = buildService.getBuildFileStream(build);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + build.getFileName() + "\"")
                .body(buildFile);
    }

    @GetMapping("/builds/latest/download")
    public ResponseEntity<Resource> downloadLatestBuild(@PathVariable String projectName,
                                                        @PathVariable String versionName) {
        Build latestBuild = buildService.getLatestBuild(projectName, versionName);
        Resource buildFile = buildService.getBuildFileStream(latestBuild);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + latestBuild.getFileName() + "\"")
                .body(buildFile);
    }
}
