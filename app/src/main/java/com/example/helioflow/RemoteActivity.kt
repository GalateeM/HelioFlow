package com.example.helioflow

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.sql.Connection
import java.sql.DriverManager
import java.sql.Timestamp

class RemoteActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "RemoteActivity"
    }

    private var selectedRoom: String? = null

    // TODO: Remplir les paramètres de connexion PostgreSQL
    private val DB_URL = "jdbc:postgresql://postgresql-helioflow.alwaysdata.net:5432/helioflow_trigger?maxResultBuffer=0"
    private val DB_USER = "helioflow_android"
    private val DB_PASSWORD = "z5yGpK%n03I2BW"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_remote)

        val btnSalon = findViewById<Button>(R.id.btn_salon)
        val btnChambre = findViewById<Button>(R.id.btn_chambre)
        val btnUp = findViewById<Button>(R.id.btn_up)
        val btnDown = findViewById<Button>(R.id.btn_down)

        btnSalon.setOnClickListener {
            selectedRoom = "salon"
            updateRoomSelection(btnSalon, btnChambre)
            Toast.makeText(this, "Salon sélectionné", Toast.LENGTH_SHORT).show()
        }

        btnChambre.setOnClickListener {
            selectedRoom = "chambre"
            updateRoomSelection(btnChambre, btnSalon)
            Toast.makeText(this, "Chambre sélectionnée", Toast.LENGTH_SHORT).show()
        }

        btnUp.setOnClickListener {
            val room = selectedRoom
            if (room == null) {
                Toast.makeText(this, "Veuillez d'abord sélectionner une pièce", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            sendAction(room, "open")
        }

        btnDown.setOnClickListener {
            val room = selectedRoom
            if (room == null) {
                Toast.makeText(this, "Veuillez d'abord sélectionner une pièce", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            sendAction(room, "close")
        }
    }

    private fun updateRoomSelection(selected: Button, other: Button) {
        val selectedColor = ContextCompat.getColor(this, R.color.teal_200)
        val defaultColor = ContextCompat.getColor(this, R.color.purple_500)
        selected.setBackgroundColor(selectedColor)
        other.setBackgroundColor(defaultColor)
    }

    private fun sendAction(room: String, action: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                Class.forName("org.postgresql.Driver")
                Log.d(TAG, "Connecting to $DB_URL")
                val connection: Connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)
                Log.d(TAG, "Connected successfully")

                val sql = "INSERT INTO remote_actions (action, params_action, device) VALUES (?, '', ?)"
                Log.d(TAG, "Executing SQL: $sql, params: action=$action")
                val preparedStatement = connection.prepareStatement(sql)
                preparedStatement.setString(1, action)
                preparedStatement.setString(2, room)
                val rows = preparedStatement.executeUpdate()
                Log.d(TAG, "Rows affected: $rows")

                preparedStatement.close()
                connection.close()

                withContext(Dispatchers.Main) {
                    Toast.makeText(this@RemoteActivity, "Volets $action : $room", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error inserting remote action", e)
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@RemoteActivity, "Erreur : ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}
