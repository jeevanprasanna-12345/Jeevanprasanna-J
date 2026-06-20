package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.example.ui.PortalViewModel
import com.example.ui.Screen
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.EventDetailsScreen
import com.example.ui.screens.LoginScreen
import com.example.ui.screens.RegisterScreen
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
    
    // Instantiate our Room-connected state controllers using simple constructor factories
    private val viewModel: PortalViewModel by viewModels { 
        PortalViewModel.Factory(application) 
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Setup edge-to-edge viewport parameters
        enableEdgeToEdge()

        setContent {
            MyApplicationTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val currentScreen by viewModel.currentScreen.collectAsState()

                    // Handle application routing page-to-page securely
                    when (val screen = currentScreen) {
                        is Screen.Login -> LoginScreen(viewModel = viewModel)
                        is Screen.Register -> RegisterScreen(viewModel = viewModel)
                        is Screen.Dashboard -> DashboardScreen(viewModel = viewModel)
                        is Screen.EventDetails -> EventDetailsScreen(
                            eventId = screen.eventId, 
                            viewModel = viewModel
                        )
                    }
                }
            }
        }
    }
}
