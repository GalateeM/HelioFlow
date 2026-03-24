package com.example.helioflow

import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class RemoteActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_remote)

        val btnSalon = findViewById<Button>(R.id.btn_salon)
        val btnChambre = findViewById<Button>(R.id.btn_chambre)
        val btnUp = findViewById<Button>(R.id.btn_up)
        val btnDown = findViewById<Button>(R.id.btn_down)

        btnSalon.setOnClickListener {
            Toast.makeText(this, "Salon", Toast.LENGTH_SHORT).show()
        }

        btnChambre.setOnClickListener {
            Toast.makeText(this, "Chambre", Toast.LENGTH_SHORT).show()
        }

        btnUp.setOnClickListener {
            Toast.makeText(this, "Haut", Toast.LENGTH_SHORT).show()
        }

        btnDown.setOnClickListener {
            Toast.makeText(this, "Bas", Toast.LENGTH_SHORT).show()
        }
    }
}