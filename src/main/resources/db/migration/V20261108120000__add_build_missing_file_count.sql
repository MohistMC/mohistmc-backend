-- how many times the build was found without its file, see BuildFileCheckCron
ALTER TABLE build ADD COLUMN missing_file_count INTEGER NOT NULL DEFAULT 0;
