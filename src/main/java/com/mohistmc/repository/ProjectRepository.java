package com.mohistmc.repository;

import com.mohistmc.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ProjectRepository extends JpaRepository<Project, Integer> {
    @Query("""
            SELECT p FROM Project p JOIN FETCH p.projectVersions pv WHERE p.active = true AND pv.active = true
            """)
    List<Project> findAllWithVersions();
}