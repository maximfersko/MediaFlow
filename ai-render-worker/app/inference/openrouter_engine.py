from .base import InferenceEngine, InferenceResult, ProgressCallback


class OpenRouterEngine(InferenceEngine):

    def __init__(self, api_key: str):
        # TODO: init AsyncOpenAI client
        self.api_key = api_key

    async def generate_image(
        self, job_id: str, prompt: str, model_config: dict, on_progress: ProgressCallback
    ) -> InferenceResult:
        raise NotImplementedError

    async def generate_text(
        self, job_id: str, prompt: str, model_config: dict, on_progress: ProgressCallback
    ) -> InferenceResult:
        raise NotImplementedError
