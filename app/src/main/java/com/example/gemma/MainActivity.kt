package com.example.gemma

import android.app.Application
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import com.example.gemma.ui.theme.GemmaTheme

class MainActivity : ComponentActivity() {
    private val viewModel by viewModels<ChatViewModel>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            GemmaTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                   MainScreen(modifier = Modifier.padding(innerPadding), viewModel = viewModel)
                }
            }
        }
    }
}

