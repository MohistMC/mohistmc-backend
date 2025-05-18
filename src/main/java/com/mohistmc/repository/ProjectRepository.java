package com.mohistmc.repository;

import com.mohistmc.entity.Project;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ProjectRepository extends JpaRepository<Project, Integer> {
    @EntityGraph(attributePaths = {"projectVersions"})
    @Query("SELECT p FROM Project p")
    List<Project> findAllWithVersions();
}