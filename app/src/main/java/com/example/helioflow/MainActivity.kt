package com.example.helioflow

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.helioflow.ui.theme.HelioFlowTheme
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

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
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    HelioFlowTheme {
        Greeting("Android")
    }
}