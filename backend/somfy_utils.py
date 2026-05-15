import os
import requests
from dotenv import load_dotenv

load_dotenv()
TOKEN = os.getenv("SOMFY_TOKEN")
DEVICE_SALON = os.getenv("DEVICE_URL_SALON")
DEVICE_CHAMBRE = os.getenv("DEVICE_URL_CHAMBRE")
SOMFY_URL = os.getenv("SOMFY_API_URL")

def execute_somfy(command_name, params):
    parsed_params = [
        int(p.strip()) if p.strip().isdigit()
        else p.strip().replace('"', '')
        for p in params.split(',')
    ]

    payloadSalon = {
        "label": "Open Salon",
        "actions": [
            {
                "deviceURL": DEVICE_SALON,
                "commands": [
                    {
                        "name": command_name,
                        "parameters": parsed_params
                    }
                ]
            }
        ]
    }

    payloadChambre = {
        "label": "Open Chambre",
        "actions": [
            {
                "deviceURL": DEVICE_CHAMBRE,
                "commands": [
                    {
                        "name": command_name,
                        "parameters": parsed_params
                    }
                ]
            }
        ]
    }

    headers = {
        "Authorization": f"Bearer {TOKEN}",
        "Content-Type": "application/json"
    }

    try:
        response = requests.post(SOMFY_URL, json=payloadSalon, headers=headers, verify=False)

        if response.status_code == 200:
            print("✅ Commande Somfy envoyée avec succès")
        else:
            print("❌ Erreur Somfy :", response.status_code)
            print(response.text)

        response = requests.post(SOMFY_URL, json=payloadChambre, headers=headers, verify=False)

        if response.status_code == 200:
            print("✅ Commande Somfy envoyée avec succès")
        else:
            print("❌ Erreur Somfy :", response.status_code)
            print(response.text)

    except Exception as e:
        print("❌ Exception appel Somfy :", e)