from enum import Enum
from pydantic import BaseModel


class GenerationType(str, Enum):
    IMAGE = "IMAGE"
    TEXT = "TEXT"
    AUDIO = "AUDIO"


class GenerationRequest(BaseModel):
    job_id: str
    user_id: str
    prompt: str
    type: GenerationType
    model_config: dict = {}
    idempotency_key: str | None = None


class GenerationEvent(BaseModel):
    job_id: str
    status: str          # PROCESSING | SUCCESS | FAILED
    progress: int = 0
    result_url: str | None = None
    error_message: str | None = None
    worker_id: str | None = None
