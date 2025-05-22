package com.mohistmc.repository;

import com.mohistmc.entity.BuildDownloadStat;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BuildDownloadStatsRepository extends JpaRepository<BuildDownloadStat, Integer> {
}