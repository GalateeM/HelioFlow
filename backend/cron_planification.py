import mysql.connector
from datetime import datetime
from dotenv import load_dotenv
import os
from somfy_utils import execute_somfy

# CONFIGURATION MYSQL
load_dotenv()
DB_USER = os.getenv("MYSQL_DB_USER")
DB_PASS = os.getenv("MYSQL_DB_PASS")
DB_HOST = os.getenv("MYSQL_DB_HOST")
DB_NAME = os.getenv("MYSQL_DB_NAME")
DB_CONFIG = {
    "host": DB_HOST,
    "user": DB_USER,
    "password": DB_PASS,
    "database": DB_NAME
}

DAY_MAPPING = {
    0: "L",
    1: "Ma",
    2: "Me",
    3: "J",
    4: "V",
    5: "S",
    6: "D"
}


def main():
    now = datetime.now()
    current_day_code = DAY_MAPPING[now.weekday()]
    current_time_str = now.strftime("%Hh%M")

    print("=================================")
    print("Cron lancé à :", now)
    print("Jour actuel :", current_day_code)
    print("Heure actuelle :", current_time_str)
    print("=================================")

    try:
        conn = mysql.connector.connect(**DB_CONFIG)
        cursor = conn.cursor(dictionary=True)

        cursor.execute("SELECT * FROM Programmations")

        programmations = cursor.fetchall()

        print(f"{len(programmations)} programmations trouvées.\n")

        for prog in programmations:

            # Nettoyage et séparation des jours
            days_list = [d.strip() for d in prog["days"].split(",")]
            if current_day_code in days_list and prog["time"] == current_time_str:
                print(f"À exécuter : action={prog['action']}-{prog['params_action']}")
                execute_somfy(prog["action"], prog['params_action'])


        cursor.close()
        conn.close()

    except Exception as e:
        print("ERREUR :", e)

    print("CRON TERMINÉ\n")

if __name__ == "__main__":
    main()