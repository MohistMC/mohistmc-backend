INSERT INTO project (name, repository_owner, repository_name, git_platform)
VALUES ('youer', 'mohistmc', 'youer', 'GITHUB');

INSERT INTO project_version (project_id, version_name, git_branch)
VALUES
    (3, '1.21.1', '1.21.1');

/* Disable 1.21.4 and 1.21.5 versions for Banner */
UPDATE project_version
SET active = false
WHERE version_name IN ('1.21.4', '1.21.5')
  AND project_id = 2;