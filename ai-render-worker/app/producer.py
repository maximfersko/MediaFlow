class EventProducer:
    def __init__(self, kafka_producer):
        self.producer = kafka_producer

    async def send_progress(self, job_id: str, progress: int) -> None:
        raise NotImplementedError

    async def send_success(self, job_id: str, result_url: str) -> None:
        raise NotImplementedError

    async def send_failed(self, job_id: str, error_message: str) -> None:
        raise NotImplementedError
