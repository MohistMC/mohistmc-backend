package com.mohistmc.service;

import com.mohistmc.config.properties.FolderProperties;
import com.mohistmc.entity.Build;
import com.mohistmc.repository.BuildRepository;
import com.mohistmc.util.FileUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BuildService {
    private final BuildRepository buildRepository;

    private final FolderProperties folderProperties;

    public List<Build> getBuildsByProjectAndVersion(String projectName, String versionName) {
        return buildRepository.findAllByProjectVersion_ProjectNameAndProjectVersion_VersionName(projectName, versionName);
    }

    public Build getBuildById(Integer buildId) {
        return buildRepository.findById(buildId).orElseThrow(() -> new RuntimeException("Build not found"));
    }

    public Build getLatestBuild(String projectName, String versionName) {
        return buildRepository.findAllByProjectVersion_ProjectNameAndProjectVersion_VersionName(projectName, versionName)
                .stream()
                .max(Comparator.comparing(Build::getCreatedAt))
                .orElseThrow(() -> new RuntimeException("No builds found"));
    }

    public Resource getBuildFileStream(Build build) {
        Path buildPath = folderProperties.getBuildPath(build);
        return FileUtil.getFileStream(buildPath);
    }
}
