import os
import requests
from dotenv import load_dotenv

load_dotenv()
TOKEN = os.getenv("SOMFY_TOKEN")
DEVICE_SALON = os.getenv("DEVICE_URL_SALON")
DEVICE_CHAMBRE = os.getenv("DEVICE_URL_CHAMBRE")
SOMFY_URL = os.getenv("SOMFY_API_URL")

def execute_somfy(command_name, params, device):
    parsed_params = [
        int(p.strip()) if p.strip().isdigit()
        else p.strip().replace('"', '')
        for p in params.split(',')
    ] if params.strip() else []

    if device == "salon":
        label = "Open Salon"
        deviceURL = DEVICE_SALON

    else:
        label = "Open Chambre",
        deviceURL = DEVICE_CHAMBRE
        

    payload = {
        "label": label,
        "actions": [
            {
                "deviceURL": deviceURL,
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
        print(payload)
        response = requests.post(SOMFY_URL, json=payload, headers=headers, verify=False)

        if response.status_code == 200:
            print("✅ Commande Somfy envoyée avec succès")
        else:
            print("❌ Erreur Somfy :", response.status_code)
            print(response.text)

    except Exception as e:
        print("❌ Exception appel Somfy :", e)