CREATE TABLE IF NOT EXISTS locations
(
    id   BIGSERIAL PRIMARY KEY,
    slug VARCHAR(128) NOT NULL,
    name VARCHAR(128) NOT NULL
);

CREATE TABLE IF NOT EXISTS events
(
    id       BIGSERIAL PRIMARY KEY,
    name     VARCHAR(128)             NOT NULL,
    date     TIMESTAMP WITH TIME ZONE NOT NULL,
    location_id BIGINT REFERENCES locations (id) ON DELETE CASCADE
);

-- rollback DROP TABLE locations
-- rollback DROP TABLE events