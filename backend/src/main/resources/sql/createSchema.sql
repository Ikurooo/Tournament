CREATE TABLE IF NOT EXISTS breed
(
    id   BIGINT PRIMARY KEY,
    name VARCHAR(32) UNIQUE NOT NULL
);

CREATE TABLE IF NOT EXISTS horse
(
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    name          VARCHAR(255)            NOT NULL,
    -- Instead of an ENUM (H2 specific) this could also be done with a character string type and a check constraint.
    sex           ENUM ('MALE', 'FEMALE') NOT NULL,
    date_of_birth DATE                    NOT NULL,
    height        NUMERIC(4, 2) NOT NULL,
    weight        NUMERIC(7, 2) NOT NULL,
    // TODO handle optional everywhere
    breed_id      BIGINT REFERENCES breed (id)
);

CREATE TABLE IF NOT EXISTS tournament
(
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    name       VARCHAR(255) NOT NULL,
    start_date DATE         NOT NULL,
    end_date   DATE         NOT NULL
);

CREATE TABLE IF NOT EXISTS horse_tourney_linker
(
    horse_id BIGINT REFERENCES horse (id),
    tournament_id BIGINT REFERENCES tournament (id),
    PRIMARY KEY (horse_id, tournament_id),
    round_reached BIGINT  CHECK (round_reached IS NULL OR round_reached >= 0 OR round_reached <= 4),
    entry_number BIGINT
);
