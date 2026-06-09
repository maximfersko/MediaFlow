import asyncio

from .base import InferenceEngine, InferenceResult, ProgressCallback


class MockEngine(InferenceEngine):
    """Stub for unit tests — no real API calls."""

    async def generate_image(
        self, job_id: str, prompt: str, model_config: dict, on_progress: ProgressCallback
    ) -> InferenceResult:
        for p in [25, 50, 75, 95]:
            await asyncio.sleep(0.1)
            await on_progress(job_id, p)
        return InferenceResult(data=b"fake_image_data", content_type="image/png")

    async def generate_text(
        self, job_id: str, prompt: str, model_config: dict, on_progress: ProgressCallback
    ) -> InferenceResult:
        await asyncio.sleep(0.2)
        await on_progress(job_id, 100)
        return InferenceResult(data=b"mock text", content_type="text/plain")
