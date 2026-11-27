CREATE TABLE transfers (
    id               UUID            PRIMARY KEY,
    reference        VARCHAR(30)     NOT NULL UNIQUE,
    idempotency_key  VARCHAR(100)    NOT NULL UNIQUE,
    from_account_id  UUID            NOT NULL,
    to_account_id    UUID            NOT NULL,
    amount           NUMERIC(19, 2)  NOT NULL CHECK (amount > 0),
    currency         CHAR(3)         NOT NULL,
    status           VARCHAR(20)     NOT NULL
                     CHECK (status IN ('PENDING', 'DEBITED', 'COMPLETED', 'REFUNDING', 'FAILED')),
    failure_reason   VARCHAR(255),
    version          BIGINT          NOT NULL DEFAULT 0,
    created_at       TIMESTAMPTZ     NOT NULL,
    updated_at       TIMESTAMPTZ     NOT NULL,
    CHECK (from_account_id <> to_account_id)
);

CREATE INDEX idx_transfers_status ON transfers (status);