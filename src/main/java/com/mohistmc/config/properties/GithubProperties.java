package com.mohistmc.config.properties;

import org.kohsuke.github.GitHub;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@ConfigurationProperties(prefix = "github")
public class GithubProperties {
    @Value("${github.username}")
    private String username;

    @Value("${github.token}")
    private String token;

    private static GitHub connectAnonymously() {
        try {
            return GitHub.connectAnonymously();
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    public GitHub connect() {
        try {
            return GitHub.connect(username, token);
        } catch (Exception e) {
            return connectAnonymously();
        }
    }
}
