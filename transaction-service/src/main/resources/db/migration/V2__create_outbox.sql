CREATE TABLE outbox_events (
    id           UUID          PRIMARY KEY,
    topic        VARCHAR(100)  NOT NULL,
    message_key  VARCHAR(100)  NOT NULL,
    payload      TEXT          NOT NULL,
    created_at   TIMESTAMPTZ   NOT NULL,
    sent_at      TIMESTAMPTZ
);

-- Only unsent rows are indexed → the relay's query stays fast even with millions of old rows
CREATE INDEX idx_outbox_unsent ON outbox_events (created_at) WHERE sent_at IS NULL;