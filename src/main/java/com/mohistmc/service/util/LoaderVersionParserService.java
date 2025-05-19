package com.mohistmc.service.util;

import com.mohistmc.entity.Build;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class LoaderVersionParserService {
    public static final Pattern PATTERN_FABRIC_LOADER = Pattern.compile("\"fabricloader\"\\s*:\\s*\"([^\"]+)\"");
    private final RestTemplate restTemplate = new RestTemplate();

    private String getPageContent(String url) {
        try {
            return restTemplate.getForObject(url, String.class);
        } catch (Exception e) {
            return null;
        }
    }

    public String parseForgeVersion(Build build) {
        String url = String.format("https://raw.githubusercontent.com/%s/%s/%s/build.gradle",
                build.getProjectVersion().getProject().getRepositoryOwner(),
                build.getProjectVersion().getProject().getRepositoryName(),
                build.getGitInfo().getGitSha());
        String buildGradleContent = getPageContent(url);

        if (buildGradleContent != null) {
            String normalized = buildGradleContent.replaceAll(" ", "")
                    .replaceAll("'", "\"")
                    .replaceAll("\n", "");

            String[] splitters = {
                    "forge_version=\"",
                    "\"FORGE_VERSION\",\"",
                    "ext.forgeVersion=\"",
                    "FORGE_VERSION=\""
            };

            for (String splitter : splitters) {
                String[] parts = normalized.split(splitter);
                if (parts.length > 1) {
                    String[] after = parts[1].split("\"");
                    if (after.length > 0) {
                        return after[0];
                    }
                }
            }
        }

        // Fallback to gradle.properties
        String gradlePropsUrl = String.format("https://raw.githubusercontent.com/%s/%s/%s/gradle.properties",
                build.getProjectVersion().getProject().getRepositoryOwner(),
                build.getProjectVersion().getProject().getRepositoryName(),
                build.getGitInfo().getGitSha());
        String gradlePropsContent = getPageContent(gradlePropsUrl);

        if (gradlePropsContent != null) {
            for (String line : gradlePropsContent.split("\n")) {
                if (line.startsWith("forge_version=")) {
                    return line.split("=")[1];
                }
            }
        }

        return null;
    }

    public String parseNeoForgeVersion(Build build) {
        String gradlePropsUrl = String.format("https://raw.githubusercontent.com/%s/%s/%s/gradle.properties",
                build.getProjectVersion().getProject().getRepositoryOwner(),
                build.getProjectVersion().getProject().getRepositoryName(),
                build.getGitInfo().getGitSha());
        String gradlePropsContent = getPageContent(gradlePropsUrl);

        if (gradlePropsContent != null) {
            for (String line : gradlePropsContent.split("\n")) {
                if (line.startsWith("neoforge_version=")) {
                    return line.split("=")[1];
                }
            }
        }

        return null;
    }

    public String parseFabricVersion(Build build) {
        String gradleUrl = String.format(
                "https://raw.githubusercontent.com/%s/%s/%s/gradle.properties",
                build.getProjectVersion().getProject().getRepositoryOwner(),
                build.getProjectVersion().getProject().getRepositoryName(),
                build.getGitInfo().getGitSha());
        String gradleContent = getPageContent(gradleUrl);

        if (gradleContent != null) {
            Pattern pattern = Pattern.compile("loader_version=(\\d+\\.\\d+\\.\\d+)");
            Matcher matcher = pattern.matcher(gradleContent);
            if (matcher.find()) {
                return matcher.group(1);
            }
        }

        // If loader_version not found in gradle.properties, try fabric.mod.json
        String modUrl = String.format(
                "https://raw.githubusercontent.com/%s/%s/%s/src/main/resources/fabric.mod.json",
                build.getProjectVersion().getProject().getRepositoryOwner(),
                build.getProjectVersion().getProject().getRepositoryName(),
                build.getGitInfo().getGitSha());
        String modContent = getPageContent(modUrl);

        if (modContent != null) {
            Matcher matcher = PATTERN_FABRIC_LOADER.matcher(modContent);
            if (matcher.find()) {
                return matcher.group(1);
            }
        }

        return null;
    }
}
