package com.mohistmc.service.stats;

import com.mohistmc.config.properties.GithubProperties;
import com.mohistmc.dto.stats.GithubStats;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.kohsuke.github.GHIssueState;
import org.kohsuke.github.GitHub;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
@Getter
public class GithubStatsService {
    private final GithubProperties githubProperties;

    private GithubStats githubStats = new GithubStats(0, 0);

    public void synchronize() {
        GitHub githubConnection = githubProperties.connect();

        int totalOpenIssues = githubProperties.getStatsRepositories().stream().map(repo -> {
            try {
                var ghRepo = githubConnection.getRepository(repo);
                return ghRepo.getOpenIssueCount();
            } catch (Exception e) {
                log.error("Error fetching stats for repository {}: {}", repo, e.getMessage(), e);
                return 0;
            }
        }).reduce(0, Integer::sum);

        int totalClosedIssues = githubProperties.getStatsRepositories().stream().map(repo -> {
            try {
                var ghRepo = githubConnection.getRepository(repo);
                return ghRepo.getIssues(GHIssueState.CLOSED).size();
            } catch (Exception e) {
                log.error("Error fetching stats for repository {}: {}", repo, e.getMessage(), e);
                return 0;
            }
        }).reduce(0, Integer::sum);

        githubStats = new GithubStats(totalOpenIssues, totalClosedIssues);
    }
}
