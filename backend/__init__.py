from flask import Flask
import pymysql
app = Flask(__name__)
import os

DB_USER = os.getenv("DB_USER")
DB_PASS = os.getenv("DB_PASS")
DB_HOST = os.getenv("DB_HOST")
DB_NAME = os.getenv("DB_NAME")

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
def get_programmations():
    conn = get_db_connection()
    cursor = conn.cursor()
    cursor.execute("SELECT * FROM Programmations")
    rows = cursor.fetchall()
    cursor.close()
    conn.close()
    return rows

if __name__ == '__main__':
    app.run(debug=True)