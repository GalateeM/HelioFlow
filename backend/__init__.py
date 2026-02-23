from flask import Flask, request, jsonify
import pymysql
app = Flask(__name__)
import os
from functools import wraps


DB_USER = os.getenv("DB_USER")
DB_PASS = os.getenv("DB_PASS")
DB_HOST = os.getenv("DB_HOST")
DB_NAME = os.getenv("DB_NAME")
API_TOKEN = os.getenv("API_TOKEN")

def require_token(f):
    @wraps(f)
    def decorated(*args, **kwargs):
        auth_header = request.headers.get("Authorization")

        if not auth_header:
            return jsonify({"error": "Token manquant"}), 401

        try:
            scheme, token = auth_header.split()
            if scheme.lower() != "bearer" or token != API_TOKEN:
                return jsonify({"error": "Token invalide"}), 403
        except ValueError:
            return jsonify({"error": "Header Authorization invalide"}), 401

        return f(*args, **kwargs)

    return decorated

def get_db_connection():
    """
    Retourne une connexion MySQL.
    """
    return pymysql.connect(
        host=DB_HOST,
        user=DB_USER,
        password=DB_PASS,
        database=DB_NAME,
        cursorclass=pymysql.cursors.DictCursor
    )

@app.route('/test')
def hello_test():
    return {'message': 'Hello, TEST!'}

@app.route("/programmations", methods=["GET"])
@require_token
def get_programmations():
    conn = get_db_connection()
    cursor = conn.cursor()
    cursor.execute("SELECT * FROM Programmations")
    rows = cursor.fetchall()
    cursor.close()
    conn.close()
    if rows:
        return rows
    else:
        return {}

@app.route('/programmations', methods=['POST'])
@require_token
def create_programmation():
    data = request.json
    
    action = data["action"]
    days = data["days"]
    time = data["time"]
    
    conn = get_db_connection()
    cursor = conn.cursor()
    
    cursor.execute(
        "INSERT INTO Programmations (action, days, time) VALUES (%s, %s, %s)",
        (action, days, time)
    )
    
    conn.commit()
    cursor.close()
    conn.close()
    
    return jsonify({"status": "Programmation enregistrée"})
    

if __name__ == '__main__':
    app.run(debug=True)