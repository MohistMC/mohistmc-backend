package com.mohistmc.service;

import com.mohistmc.entity.Build;
import com.mohistmc.entity.BuildDownloadStat;
import com.mohistmc.repository.BuildDownloadStatsRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BuildDownloadStatsService {
    private final BuildDownloadStatsRepository buildDownloadStatsRepository;

    public void saveBuildDownload(Build build, HttpServletRequest request) {
        String ipAddress = request.getRemoteAddr();
        String ipUnderProxy = request.getHeader("X-Forwarded-For");
        String userAgent = request.getHeader("User-Agent");

        BuildDownloadStat buildDownloadStat = new BuildDownloadStat()
                .setIp(ipUnderProxy != null ? ipUnderProxy : ipAddress)
                .setUserAgent(userAgent)
                .setBuild(build);

        buildDownloadStatsRepository.save(buildDownloadStat);
    }
}
