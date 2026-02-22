package com.example.helioflow

import android.os.Bundle
import android.view.LayoutInflater
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.helioflow.api.ShutterApiClient
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainActivity : AppCompatActivity() {

    private lateinit var tokenManager: TokenManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        tokenManager = TokenManager(this)
        ShutterApiClient.initialize(this)

        if (savedInstanceState == null) {
            val fragment = ShutterRulesFragment.newInstance()
            supportFragmentManager.beginTransaction()
                .replace(R.id.shutter_rules_fragment_container, fragment)
                .commit()
        }

        val addButton = findViewById<FloatingActionButton>(R.id.add_rule_button)

        addButton.setOnClickListener {

            val fragment = supportFragmentManager
                .findFragmentById(R.id.shutter_rules_fragment_container)
                    as? ShutterRulesFragment

            fragment?.addNewRule()
        }

        val settingsButton = findViewById<ImageButton>(R.id.settings_button)
        settingsButton.setOnClickListener {
            showTokenDialog()
        }

        if (!tokenManager.hasToken()) {
            showTokenDialog()
        }
    }

    private fun showTokenDialog() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_token, null)
        val tokenEditText = dialogView.findViewById<EditText>(R.id.token_edit_text)

        val currentToken = tokenManager.getToken()
        if (!currentToken.isNullOrEmpty()) {
            tokenEditText.setText(currentToken)
        }

        val dialog = AlertDialog.Builder(this)
            .setTitle(R.string.token_dialog_title)
            .setView(dialogView)
            .setPositiveButton(R.string.validate, null)
            .setNegativeButton(R.string.cancel, null)
            .create()

        dialog.setOnShowListener {
            val positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE)
            positiveButton.setOnClickListener {
                val token = tokenEditText.text.toString().trim()
                if (token.isEmpty()) {
                    tokenEditText.error = getString(R.string.token_required)
                    return@setOnClickListener
                }

                tokenManager.saveToken(token)
                Toast.makeText(this, R.string.token_saved, Toast.LENGTH_SHORT).show()
                dialog.dismiss()
            }
        }

        dialog.show()
    }
}