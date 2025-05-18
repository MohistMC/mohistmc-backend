package com.mohistmc.repository;

import com.mohistmc.entity.LoaderVersion;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LoaderVersionRepository extends JpaRepository<LoaderVersion, Integer> {
}