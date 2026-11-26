CREATE TABLE transfers (
    id               UUID            PRIMARY KEY,
    reference        VARCHAR(30)     NOT NULL UNIQUE,
    idempotency_key  VARCHAR(100)    NOT NULL UNIQUE,
    from_account_id  UUID            NOT NULL REFERENCES accounts (id),
    to_account_id    UUID            NOT NULL REFERENCES accounts (id),
    amount           NUMERIC(19, 2)  NOT NULL CHECK (amount > 0),
    currency         CHAR(3)         NOT NULL,
    status           VARCHAR(20)     NOT NULL CHECK (status IN ('PENDING', 'COMPLETED', 'FAILED')),
    created_at       TIMESTAMPTZ     NOT NULL,
    CHECK (from_account_id <> to_account_id)
);

CREATE INDEX idx_transfers_from ON transfers (from_account_id);
CREATE INDEX idx_transfers_to   ON transfers (to_account_id);