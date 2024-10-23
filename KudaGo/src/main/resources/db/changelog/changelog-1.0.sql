CREATE TABLE IF NOT EXISTS locationResponses
(
    id   BIGSERIAL PRIMARY KEY,
    slug VARCHAR(128) NOT NULL,
    name VARCHAR(128) NOT NULL
);

CREATE TABLE IF NOT EXISTS suitableEvents
(
    id       BIGSERIAL PRIMARY KEY,
    name     VARCHAR(128)             NOT NULL,
    date     TIMESTAMP WITH TIME ZONE NOT NULL,
    location_id BIGINT REFERENCES locationResponses (id) ON DELETE CASCADE
);