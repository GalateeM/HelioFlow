import mysql.connector
from datetime import datetime
import os

# CONFIGURATION MYSQL
DB_USER = os.getenv("DB_USER")
DB_PASS = os.getenv("DB_PASS")
DB_HOST = os.getenv("DB_HOST")
DB_NAME = os.getenv("DB_NAME")
DB_CONFIG = {
    "host": DB_HOST,
    "user": DB_USER,
    "password": DB_PASS,
    "database": DB_NAME
}

def main():
    print("===================================")
    print("CRON LECTURE DB LANCÉ")
    print("Heure actuelle :", datetime.now())
    print("===================================")

    try:
        conn = mysql.connector.connect(**DB_CONFIG)
        cursor = conn.cursor(dictionary=True)

        cursor.execute("SELECT * FROM Programmations")

        rows = cursor.fetchall()

        print(f"{len(rows)} programmations trouvées.\n")

        for row in rows:
            print("ID:", row["id"])
            print("Action:", row["action"])
            print("Execution:", row["execution_time"])
            print("----------------------")

        cursor.close()
        conn.close()

    except Exception as e:
        print("ERREUR :", e)

    print("CRON TERMINÉ\n")

if __name__ == "__main__":
    main()