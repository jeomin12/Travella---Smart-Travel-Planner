// --- FILE: ProfileSetupScreen.kt ---
package com.travelassistant.travella.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController

@Composable
fun ProfileSetupScreen(navController: NavHostController) {
    var name by remember { mutableStateOf("") }
    var passport by remember { mutableStateOf("") }
    var preferences by remember { mutableStateOf("") }

    Column(modifier = Modifier.padding(16.dp)) {
        Text("Profile Setup", fontSize = 24.sp)
        Spacer(Modifier.height(16.dp))

        OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Full Name") })
        OutlinedTextField(value = passport, onValueChange = { passport = it }, label = { Text("Passport Number") })
        OutlinedTextField(value = preferences, onValueChange = { preferences = it }, label = { Text("Travel Preferences") })

        Spacer(Modifier.height(16.dp))
        Button(onClick = {
            // Mock save and go to dashboard
            navController.navigate("dashboard")
        }) {
            Text("Save & Continue")
        }
    }
}
