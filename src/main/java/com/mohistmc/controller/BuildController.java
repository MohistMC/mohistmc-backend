package com.mohistmc.controller;

import com.mohistmc.dto.response.BuildResponseDto;
import com.mohistmc.entity.Build;
import com.mohistmc.mapper.BuildMapper;
import com.mohistmc.service.BuildDownloadStatsService;
import com.mohistmc.service.BuildService;
import com.mohistmc.service.BuildStorageService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/project/{projectName}/{versionName}")
@RequiredArgsConstructor
public class BuildController {
    private final BuildService buildService;
    private final BuildStorageService buildStorageService;
    private final BuildDownloadStatsService buildDownloadStatsService;

    private final BuildMapper buildMapper;

    @GetMapping("/builds")
    public List<BuildResponseDto> getProjectVersions(@PathVariable String projectName,
                                                     @PathVariable String versionName) {
        return buildService.getBuildsByProjectAndVersion(projectName, versionName)
                .stream()
                .sorted((b1, b2) -> b2.getBuiltOn().compareTo(b1.getBuiltOn()))
                .map(buildMapper::toDto)
                .toList();
    }

    @GetMapping("/builds/{buildId}")
    public BuildResponseDto getBuild(@PathVariable String projectName,
                                     @PathVariable String versionName,
                                     @PathVariable Integer buildId) {
        return buildMapper.toDto(buildService.getBuildById(buildId));
    }

    @GetMapping("/builds/latest")
    public BuildResponseDto getLatestBuild(@PathVariable String projectName,
                                           @PathVariable String versionName) {
        return buildMapper.toDto(buildService.getLatestBuild(projectName, versionName));
    }

    @GetMapping("/builds/{buildId}/download")
    public ResponseEntity<Void> downloadBuild(@PathVariable String projectName,
                                                  @PathVariable String versionName,
                                                  @PathVariable Integer buildId,
                                                  HttpServletRequest request) {
        return downloadResponse(buildService.getBuildById(buildId), request);
    }

    @GetMapping("/builds/latest/download")
    public ResponseEntity<Void> downloadLatestBuild(@PathVariable String projectName,
                                                        @PathVariable String versionName,
                                                        HttpServletRequest request) {
        return downloadResponse(buildService.getLatestBuild(projectName, versionName), request);
    }

    // redirects to the build's object in the bucket
    private ResponseEntity<Void> downloadResponse(Build build, HttpServletRequest request) {
        URI publicUrl = buildStorageService.getPublicUrl(build)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Build artifact is not available"));
        buildDownloadStatsService.saveBuildDownload(build, request);
        return ResponseEntity.status(HttpStatus.FOUND).location(publicUrl).build();
    }
}
