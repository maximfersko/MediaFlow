from contextlib import asynccontextmanager
from fastapi import FastAPI


@asynccontextmanager
async def lifespan(app: FastAPI):
    # TODO: load secrets from Vault
    # TODO: init Kafka consumer, MinIO client, OTel
    yield
    # TODO: graceful shutdown


app = FastAPI(title="AI Render Worker", lifespan=lifespan)


@app.get("/health")
async def health():
    return {"status": "ok"}
