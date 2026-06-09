from prometheus_client import Counter, Histogram

jobs_processed = Counter(
    "render_jobs_processed_total",
    "Total processed jobs",
    ["status"],
)

job_duration = Histogram(
    "render_job_duration_seconds",
    "Job processing duration",
    ["type"],
)

openrouter_requests = Counter(
    "render_openrouter_requests_total",
    "OpenRouter API calls",
    ["model", "status"],
)
