package com.mohistmc.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.Instant;

@Accessors(chain = true)
@Getter
@Setter
@Entity
@Table(name = "git_commit")
public class GitCommit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "git_sha", nullable = false, length = Integer.MAX_VALUE)
    private String gitSha;

    @Column(name = "git_commit_message", nullable = false, length = Integer.MAX_VALUE)
    private String gitCommitMessage;

    @Column(name = "git_commit_author", nullable = false, length = Integer.MAX_VALUE)
    private String gitCommitAuthor;

    @Column(name = "git_commit_email", length = Integer.MAX_VALUE)
    private String gitCommitEmail;

    @Column(name = "git_commit_date", nullable = false)
    private Instant gitCommitDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "project_id")
    private Project project;

}