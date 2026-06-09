from .inference.base import InferenceEngine
from .schemas import GenerationRequest


class RenderConsumer:
    def __init__(self, kafka_consumer, engine: InferenceEngine, producer, minio, redis):
        self.consumer = kafka_consumer
        self.engine = engine
        self.producer = producer
        self.minio = minio
        self.redis = redis

    async def start(self):
        await self.consumer.start()
        try:
            async for message in self.consumer:
                # TODO: idempotency check via Redis SET NX
                # TODO: extract W3C traceparent from Kafka headers
                # TODO: process_job(message)
                pass
        finally:
            await self.consumer.stop()
