-- Generates account numbers: MB1000000001, MB1000000002, ...
CREATE SEQUENCE account_number_seq START WITH 1000000001 INCREMENT BY 1;

CREATE TABLE accounts (
    id              UUID            PRIMARY KEY,
    account_number  VARCHAR(20)     NOT NULL UNIQUE,
    customer_id     VARCHAR(64)     NOT NULL,
    type            VARCHAR(20)     NOT NULL CHECK (type IN ('SAVINGS', 'CURRENT')),
    status          VARCHAR(20)     NOT NULL CHECK (status IN ('ACTIVE', 'FROZEN', 'CLOSED')),
    balance         NUMERIC(19, 2)  NOT NULL CHECK (balance >= 0),
    currency        CHAR(3)         NOT NULL,
    version         BIGINT          NOT NULL DEFAULT 0,
    created_at      TIMESTAMPTZ     NOT NULL,
    updated_at      TIMESTAMPTZ     NOT NULL
);

CREATE INDEX idx_accounts_customer_id ON accounts (customer_id);