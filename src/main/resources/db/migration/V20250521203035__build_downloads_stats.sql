CREATE TABLE build_download_stats
(
    id         integer generated always as identity primary key,
    build_id   INTEGER,
    ip         TEXT,
    user_agent TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_build FOREIGN KEY (build_id) REFERENCES build (id)
);
