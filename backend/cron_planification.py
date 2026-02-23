import mysql.connector
import requests
from datetime import datetime
from dotenv import load_dotenv
import os

# CONFIGURATION MYSQL
load_dotenv()
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

DAY_MAPPING = {
    0: "L",
    1: "Ma",
    2: "Me",
    3: "J",
    4: "V",
    5: "S",
    6: "D"
}

def execute_somfy(command_name):
    token = os.getenv("SOMFY_TOKEN")
    device_salon = os.getenv("DEVICE_URL_SALON")
    device_chambre = os.getenv("DEVICE_URL_CHAMBRE")
    somfy_url = os.getenv("SOMFY_API_URL")

    payloadSalon = {
        "label": "Open Salon",
        "actions": [
            {
                "deviceURL": device_salon,
                "commands": [
                    {
                        "name": command_name,
                        "parameters": []
                    }
                ]
            }
        ]
    }

    payloadChambre = {
        "label": "Open Chambre",
        "actions": [
            {
                "deviceURL": device_chambre,
                "commands": [
                    {
                        "name": command_name,
                        "parameters": []
                    }
                ]
            }
        ]
    }

    headers = {
        "Authorization": f"Bearer {token}",
        "Content-Type": "application/json"
    }

    try:
        response = requests.post(somfy_url, json=payloadSalon, headers=headers)

        if response.status_code == 200:
            print("✅ Commande Somfy envoyée avec succès")
        else:
            print("❌ Erreur Somfy :", response.status_code)
            print(response.text)

        response = requests.post(somfy_url, json=payloadChambre, headers=headers)

        if response.status_code == 200:
            print("✅ Commande Somfy envoyée avec succès")
        else:
            print("❌ Erreur Somfy :", response.status_code)
            print(response.text)

    except Exception as e:
        print("❌ Exception appel Somfy :", e)



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
                print(f"À exécuter : action={prog['action']}")
                execute_somfy(prog["action"])


        cursor.close()
        conn.close()

    except Exception as e:
        print("ERREUR :", e)

    print("CRON TERMINÉ\n")

if __name__ == "__main__":
    main()