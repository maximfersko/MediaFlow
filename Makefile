include .env
export

COMPOSE = docker compose -f infra/docker-compose.yml --env-file .env

infra:
	$(COMPOSE) up -d postgres redis redis-insight zookeeper kafka kafka-init minio vault vault-init keycloak

infra-down:
	$(COMPOSE) down

gateway:
	$(COMPOSE) up -d --build api-gateway

orchestrator:
	$(COMPOSE) up -d --build db-migrator job-orchestrator

worker:
	$(COMPOSE) up -d --build ai-render-worker

notifications:
	$(COMPOSE) up -d --build notification-service

observability:
	$(COMPOSE) up -d tempo loki prometheus grafana

all:
	$(COMPOSE) up -d --build

logs:
	$(COMPOSE) logs -f $(s)

ps:
	$(COMPOSE) ps

vault-init:
	$(COMPOSE) run --rm vault-init

token:
	@curl -s -X POST http://localhost:8180/realms/mediaflow/protocol/openid-connect/token \
		-d "grant_type=password&client_id=mediaflow-gateway&client_secret=$${KEYCLOAK_CLIENT_SECRET}&username=testuser&password=testpass" \
		| jq -r '.access_token' | tr -d '\n'
