create table IF NOT EXISTS project
(
    id               integer generated always as identity
        primary key,
    name             text,
    repository_owner text,
    repository_name  text,
    git_platform     text
        constraint chk_git_platform
            check (git_platform = ANY (ARRAY['GITHUB'::text, 'GITLAB'::text])),
    active           boolean default true
);

comment
on table project is 'Project table';

alter table project
    owner to mohistmc;

CREATE TABLE IF NOT EXISTS git_commit
(
    id                 INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    git_sha            TEXT      NOT NULL,
    git_commit_message TEXT      NOT NULL,
    git_commit_author  TEXT      NOT NULL,
    git_commit_email   TEXT,
    git_commit_date    TIMESTAMP NOT NULL,
    project_id         INTEGER
        CONSTRAINT fk_project
            REFERENCES project
            ON DELETE CASCADE
);

COMMENT
ON TABLE git_commit IS 'Git commit information';

ALTER TABLE git_commit
    OWNER TO mohistmc;

create table IF NOT EXISTS project_version
(
    id           integer generated always as identity
        primary key,
    project_id   integer
        constraint fk_project
            references project
            on delete cascade,
    version_name text,
    git_branch   text,
    active       boolean default true
);

comment
on table project_version is 'Versions list associated to a project';

alter table project_version
    owner to mohistmc;


create table IF NOT EXISTS loader_version
(
    id               integer generated always as identity
        primary key,
    forge_version    text,
    neoforge_version text,
    fabric_version   text
);

comment
on table loader_version is 'Forge/NeoForge/Fabric loader version';

alter table loader_version
    owner to mohistmc;

-- Modification de la table build
DROP TABLE IF EXISTS build CASCADE;

CREATE TABLE IF NOT EXISTS build
(
    id                  INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    project_version_id  INTEGER
        CONSTRAINT fk_project_version
            REFERENCES project_version
            ON DELETE CASCADE,
    artifact_id         BIGINT,
    workflow_run_id     BIGINT,
    artifact_downloaded BOOLEAN   DEFAULT FALSE,
    git_info            INTEGER
        CONSTRAINT fk_git_commit
            REFERENCES git_commit (id)
            ON DELETE SET NULL,
    loader_version_id   INTEGER
        CONSTRAINT fk_loader_version
            REFERENCES loader_version (id)
            ON DELETE SET NULL,
    file_sha256         TEXT,
    built_on            TIMESTAMP,
    active              BOOLEAN   DEFAULT TRUE,
    created_at          TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at          TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

COMMENT
ON TABLE build IS 'Project builds';

ALTER TABLE build
    OWNER TO mohistmc;
