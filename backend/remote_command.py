import psycopg2
import select
import json
from dotenv import load_dotenv
import os
from somfy_utils import execute_somfy

import time
import socket

def wait_for_network(host="8.8.8.8", timeout=60):
    print("Attente du réseau...")
    start = time.time()
    while time.time() - start < timeout:
        try:
            socket.setdefaulttimeout(3)
            socket.socket(socket.AF_INET, socket.SOCK_STREAM).connect((host, 53))
            print("Réseau disponible !")
            return True
        except OSError:
            time.sleep(2)
    raise Exception("Réseau non disponible après 60s")

wait_for_network()

# CONFIG POSTGRESQL
load_dotenv()
DB_USER = os.getenv("POSTGRES_DB_USER")
DB_PASS = os.getenv("POSTGRES_DB_PASS")
DB_HOST = os.getenv("POSTGRES_DB_HOST")
DB_NAME = os.getenv("POSTGRES_DB_NAME")
conn = psycopg2.connect(
    dbname=DB_NAME,
    user=DB_USER,
    password=DB_PASS,
    host=DB_HOST,
    port=5432
)

conn.set_isolation_level(psycopg2.extensions.ISOLATION_LEVEL_AUTOCOMMIT)

cur = conn.cursor()
cur.execute("LISTEN remote_action_added;")

print("📡 En attente d'événements PostgreSQL...")

while True:
    ready, _, _ = select.select([conn], [], [], 1)
    if ready:
        conn.poll()
        while conn.notifies:
            notify = conn.notifies.pop(0)
            payload = json.loads(notify.payload)

            print("🔔 Événement reçu")
            print(payload)
            execute_somfy(payload['action'], payload['params_action'], payload['device'])