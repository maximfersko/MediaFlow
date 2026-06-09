CREATE TABLE jobs (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id         VARCHAR(255) NOT NULL,
    prompt          TEXT NOT NULL,
    type            VARCHAR(50) NOT NULL,
    model_config    TEXT,
    status          VARCHAR(50) NOT NULL DEFAULT 'CREATED',
    result_url      TEXT,
    progress        INT DEFAULT 0,
    error_message   TEXT,
    quota_cost      INT NOT NULL DEFAULT 1,
    idempotency_key VARCHAR(255) UNIQUE,
    version         BIGINT NOT NULL DEFAULT 0,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_jobs_user_id_status  ON jobs(user_id, status);
CREATE INDEX idx_jobs_created_at      ON jobs(created_at DESC);
CREATE INDEX idx_jobs_idempotency_key ON jobs(idempotency_key) WHERE idempotency_key IS NOT NULL;

CREATE TABLE outbox_events (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    aggregate_id    VARCHAR(255) NOT NULL,
    aggregate_type  VARCHAR(100) NOT NULL,
    event_type      VARCHAR(100) NOT NULL,
    payload         TEXT NOT NULL,
    topic           VARCHAR(255) NOT NULL,
    trace_context   VARCHAR(512),
    status          VARCHAR(50) NOT NULL DEFAULT 'PENDING',
    retry_count     INT NOT NULL DEFAULT 0,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    processed_at    TIMESTAMPTZ
);

CREATE INDEX idx_outbox_pending ON outbox_events(status, created_at)
    WHERE status = 'PENDING';

CREATE TABLE user_quotas (
    user_id         VARCHAR(255) PRIMARY KEY,
    plan            VARCHAR(50) NOT NULL DEFAULT 'BASIC',
    monthly_limit   INT NOT NULL DEFAULT 50,
    used_this_month INT NOT NULL DEFAULT 0,
    reset_at        TIMESTAMPTZ NOT NULL,
    version         BIGINT NOT NULL DEFAULT 0
);

CREATE MATERIALIZED VIEW job_stats_daily AS
    SELECT
        DATE_TRUNC('day', created_at) AS day,
        status,
        type,
        COUNT(*) AS job_count,
        AVG(EXTRACT(EPOCH FROM (updated_at - created_at))) AS avg_duration_sec
    FROM jobs
    GROUP BY 1, 2, 3;

CREATE UNIQUE INDEX idx_job_stats_daily_unique ON job_stats_daily(day, status, type);
