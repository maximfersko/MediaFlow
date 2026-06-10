#!/bin/sh
set -e

export VAULT_ADDR="${VAULT_ADDR:-http://vault:8200}"
export VAULT_TOKEN="${VAULT_TOKEN:-root-dev-token}"

echo "Enabling KV v2 secrets engine..."
vault secrets enable -version=2 -path=secret kv || true

echo "Writing secrets for job-orchestrator / api-gateway..."
vault kv put secret/mediaflow/job-orchestrator \
  db_password="${DB_PASSWORD:-secret}" \
  redis_password="${REDIS_PASSWORD:-}" \
  kafka_sasl_password="${KAFKA_SASL_PASSWORD:-}"

vault kv put secret/mediaflow/api-gateway \
  redis_password="${REDIS_PASSWORD:-}"

echo "Writing secrets for ai-render-worker..."
vault kv put secret/mediaflow/ai-worker \
  openrouter_api_key="${OPENROUTER_API_KEY}" \
  minio_secret_key="${MINIO_SECRET_KEY:-minioadmin}"

echo "Writing secrets for keycloak..."
vault kv put secret/mediaflow/keycloak \
  client_secret="${KEYCLOAK_CLIENT_SECRET:-secret}" \
  google_client_id="${GOOGLE_CLIENT_ID:-}" \
  google_client_secret="${GOOGLE_CLIENT_SECRET:-}" \
  github_client_id="${GITHUB_CLIENT_ID:-}" \
  github_client_secret="${GITHUB_CLIENT_SECRET:-}"

echo "Writing policies..."
vault policy write job-orchestrator - <<'EOF'
path "secret/data/mediaflow/job-orchestrator" { capabilities = ["read"] }
EOF

vault policy write ai-worker - <<'EOF'
path "secret/data/mediaflow/ai-worker" { capabilities = ["read"] }
EOF

echo "Vault initialized."
