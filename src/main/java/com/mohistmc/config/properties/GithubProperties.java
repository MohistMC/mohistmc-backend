package com.mohistmc.config.properties;

import org.kohsuke.github.GitHub;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

@Component
@ConfigurationProperties(prefix = "github")
public class GithubProperties {
    @Value("${github.username}")
    private String username;

    @Value("${github.token}")
    private String token;

    @Value("${github.stats-repositories}")
    private String statsRepositories;

    private static GitHub connectAnonymously() {
        try {
            return GitHub.connectAnonymously();
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    public GitHub connect() {
        try {
            if (username == null || token == null || username.trim().isEmpty() || token.trim().isEmpty()) {
                return connectAnonymously();
            }
            return GitHub.connect(username, token);
        } catch (IOException e) {
            return connectAnonymously();
        }
    }

    public List<String> getStatsRepositories() {
        return statsRepositories == null || statsRepositories.isEmpty() ? List.of() : List.of(statsRepositories.split(","));
    }
}
