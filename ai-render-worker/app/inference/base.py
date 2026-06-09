from abc import ABC, abstractmethod
from dataclasses import dataclass
from typing import Callable, Awaitable

ProgressCallback = Callable[[str, int], Awaitable[None]]


@dataclass
class InferenceResult:
    data: bytes
    content_type: str


class InferenceEngine(ABC):

    @abstractmethod
    async def generate_image(
        self,
        job_id: str,
        prompt: str,
        model_config: dict,
        on_progress: ProgressCallback,
    ) -> InferenceResult: ...

    @abstractmethod
    async def generate_text(
        self,
        job_id: str,
        prompt: str,
        model_config: dict,
        on_progress: ProgressCallback,
    ) -> InferenceResult: ...
