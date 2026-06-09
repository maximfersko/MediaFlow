class MinioClient:
    def __init__(self, endpoint: str, access_key: str, secret_key: str, bucket: str = "results"):
        # TODO: init aioboto3 session
        self.endpoint = endpoint
        self.bucket = bucket

    async def upload_result(self, job_id: str, data: bytes, content_type: str) -> str:
        raise NotImplementedError
