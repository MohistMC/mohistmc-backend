package com.mohistmc.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Accessors(chain = true)
@Getter
@Setter
@Entity
@Table(name = "loader_version")
public class LoaderVersion {
    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "forge_version", length = Integer.MAX_VALUE)
    private String forgeVersion;

    @Column(name = "neoforge_version", length = Integer.MAX_VALUE)
    private String neoforgeVersion;

    @Column(name = "fabric_version", length = Integer.MAX_VALUE)
    private String fabricVersion;

}