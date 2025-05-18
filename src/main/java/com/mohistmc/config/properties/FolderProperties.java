package com.mohistmc.config.properties;

import com.mohistmc.entity.Build;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.nio.file.Path;

@Component
@ConfigurationProperties(prefix = "builds")
public class FolderProperties {
    @Value("${builds.path}")
    private String buildsPath;

    public Path getBuildsPath() {
        return Path.of(buildsPath);
    }

    public Path getBuildPath(Build build) {
        return getBuildsPath().resolve(build.getId().toString());
    }
}
