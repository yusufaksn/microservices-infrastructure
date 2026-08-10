CREATE TABLE IF NOT EXISTS tickets (
    id VARCHAR(36) PRIMARY KEY,
    description VARCHAR(600),
    notes VARCHAR(1000),
    assignee VARCHAR(50),
    ticket_date TIMESTAMP,
    priority_type INTEGER,
    ticket_status INTEGER,
    created_at TIMESTAMP,
    updated_at TIMESTAMP
);

CREATE TABLE IF NOT EXISTS outbox_events (
    id VARCHAR(36) PRIMARY KEY,
    aggregate_type      VARCHAR(255) NOT NULL,
    aggregate_id        VARCHAR(255) NOT NULL,
    event_type          VARCHAR(255) NOT NULL,
    payload             JSONB NOT NULL,

    -- B3 Trace
    trace_id            VARCHAR(32) NOT NULL,
    span_id             VARCHAR(16) NOT NULL,

    sampled             VARCHAR(1) NOT NULL DEFAULT '1',

    db_committed_at     TIMESTAMPTZ NOT NULL DEFAULT now()
);

ALTER TABLE outbox_events REPLICA IDENTITY FULL;