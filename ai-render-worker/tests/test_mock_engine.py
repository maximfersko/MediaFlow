import pytest
from app.inference.mock_engine import MockEngine


@pytest.mark.asyncio
async def test_mock_engine_image():
    engine = MockEngine()
    progress_calls = []

    async def on_progress(job_id: str, progress: int):
        progress_calls.append(progress)

    result = await engine.generate_image("job-1", "test prompt", {}, on_progress)

    assert result.content_type == "image/png"
    assert len(result.data) > 0
    assert progress_calls == [25, 50, 75, 95]
