package com.mohistmc.service;

import com.mohistmc.entity.Build;
import com.mohistmc.repository.BuildRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Comparator;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class BuildService {
    private final BuildRepository buildRepository;

    public List<Build> getBuildsByProjectAndVersion(String projectName, String versionName) {
        return buildRepository.findAllByProjectAndVersion(projectName, versionName);
    }

    public Build getBuildById(Integer buildId) {
        return buildRepository.findWithDetailsById(buildId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Build not found"));
    }

    public Build getLatestBuild(String projectName, String versionName) {
        return buildRepository.findAllByProjectAndVersion(projectName, versionName)
                .stream()
                .max(Comparator.comparing(Build::getCreatedAt))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No builds found"));
    }
}
