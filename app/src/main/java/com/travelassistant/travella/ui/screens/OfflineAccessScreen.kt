package com.travelassistant.travella.ui.screens


import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OfflineAccessScreen(navController: NavHostController) {
    val context = LocalContext.current
    var isDownloaded by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Offline Access") }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Access your trip information even without internet.",
                style = MaterialTheme.typography.bodyLarge
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(onClick = {
                // Placeholder: simulate download
                isDownloaded = true
                Toast.makeText(context, "Trip details downloaded for offline use!", Toast.LENGTH_SHORT).show()
            }) {
                Text("Download Trip for Offline")
            }

            Spacer(modifier = Modifier.height(24.dp))

            if (isDownloaded) {
                Text("Trip is available offline ✅")
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = {
                    Toast.makeText(context, "Viewing cached itinerary (mock)", Toast.LENGTH_SHORT).show()
                }) {
                    Text("View Offline Itinerary")
                }
                Spacer(modifier = Modifier.height(10.dp))
                Button(onClick = {
                    Toast.makeText(context, "Opening offline maps (mock)", Toast.LENGTH_SHORT).show()
                }) {
                    Text("View Offline Maps")
                }
            } else {
                Text("Trip not yet downloaded.")
            }
        }
    }
}
