package com.mohistmc.entity;

import com.mohistmc.enums.GitPlatformEnum;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.LinkedHashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "project")
public class Project {
    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "name")
    private String name;

    @Column(name = "repository_owner")
    private String repositoryOwner;

    @Column(name = "repository_name")
    private String repositoryName;

    @Enumerated(EnumType.STRING)
    @Column(name = "git_platform")
    private GitPlatformEnum gitPlatform;

    @OneToMany(mappedBy = "project", fetch = FetchType.LAZY)
    private Set<ProjectVersion> projectVersions = new LinkedHashSet<>();

}