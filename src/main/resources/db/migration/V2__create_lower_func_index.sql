ALTER TABLE users
    DROP CONSTRAINT IF EXISTS uk_users_username;

CREATE UNIQUE INDEX IF NOT EXISTS uk_users_username_lower
    ON users (LOWER(username));