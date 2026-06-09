import os
import hvac


class VaultClient:
    def __init__(self):
        self.client = hvac.Client(
            url=os.getenv("VAULT_ADDR", "http://vault:8200"),
            token=os.getenv("VAULT_TOKEN"),
        )

    def get_ai_worker_secrets(self) -> dict:
        response = self.client.secrets.kv.v2.read_secret_version(
            path="mediaflow/ai-worker",
            mount_point="secret",
        )
        return response["data"]["data"]
