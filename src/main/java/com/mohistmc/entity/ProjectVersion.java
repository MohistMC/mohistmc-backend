package com.mohistmc.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Getter
@Setter
@Entity
@Table(name = "project_version")
public class ProjectVersion {
    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "project_id")
    private Project project;

    @Column(name = "version_name", length = Integer.MAX_VALUE)
    private String versionName;

    @Column(name = "git_branch", length = Integer.MAX_VALUE)
    private String gitBranch;

    @ColumnDefault("true")
    @Column(name = "active")
    private Boolean active = true;

}