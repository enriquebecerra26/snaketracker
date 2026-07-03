package com.enriquebecerra.snaketracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.enriquebecerra.snaketracker.ui.navigation.SnakeTrackerNavHost
import com.enriquebecerra.snaketracker.ui.theme.SnakeTrackerTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SnakeTrackerTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    SnakeTrackerNavHost()
                }
            }
        }
    }
}
