import mysql.connector
from datetime import datetime

# CONFIGURATION MYSQL
DB_CONFIG = {
    "host": "mysql-helioflow.alwaysdata.net",
    "user": "helioflow_cron",
    "password": "OUI9B!Sf2lkicF",
    "database": "helioflow_bd"
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