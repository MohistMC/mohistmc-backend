package com.mohistmc.controller;

import com.mohistmc.dto.response.StatsResponseDto;
import com.mohistmc.repository.BuildDownloadStatsRepository;
import com.mohistmc.service.stats.BStatsService;
import com.mohistmc.service.stats.GithubStatsService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/stats")
@RequiredArgsConstructor
public class StatsController {
    private final BStatsService bStatsService;
    private final GithubStatsService githubStatsService;
    private final BuildDownloadStatsRepository buildDownloadStatsRepository;

    @GetMapping("/all")
    public StatsResponseDto getAllStats() {
        long downloadsCount = buildDownloadStatsRepository.count();

        return new StatsResponseDto(bStatsService.getBStats(),
                githubStatsService.getGithubStats(), downloadsCount);
    }
}
