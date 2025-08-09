package com.travelassistant.travella.ui.screens

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.travelassistant.travella.viewmodel.TripDashboardViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.InputStreamReader

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ImportFromEmailScreen(
    navController: NavHostController,
    prefilled: String = "",
    tripViewModel: TripDashboardViewModel
) {
    val context = LocalContext.current
    var emailText by remember(prefilled) { mutableStateOf(prefilled) }
    var attachmentUri by remember { mutableStateOf<Uri?>(null) }
    var attachmentContent by remember { mutableStateOf<String?>(null) }
    var attachmentFileName by remember { mutableStateOf<String?>(null) }

    val pickFileLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            attachmentUri = it
            // Attempt to read content from URI (for text-based files)
            // For PDFs/Images, you'd need a dedicated library/OCR here
            try {
                val contentResolver = context.contentResolver
                val fileName = contentResolver.query(it, null, null, null, null)?.use { cursor ->
                    val nameIndex = cursor.getColumnIndex(android.provider.OpenableColumns.DISPLAY_NAME)
                    if (nameIndex != -1 && cursor.moveToFirst()) {
                        cursor.getString(nameIndex)
                    } else null
                }
                attachmentFileName = fileName

                // Simulate reading content for text files. For PDFs/Images, this would be OCR/PDF parsing.
                val inputStream = contentResolver.openInputStream(it)
                val reader = BufferedReader(InputStreamReader(inputStream))
                val stringBuilder = StringBuilder()
                var line: String?
                while (reader.readLine().also { line = it } != null) {
                    stringBuilder.append(line).append("\n")
                }
                inputStream?.close()
                attachmentContent = stringBuilder.toString()
                Toast.makeText(context, "Attachment loaded: $fileName", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                Toast.makeText(context, "Failed to read attachment: ${e.message}", Toast.LENGTH_LONG).show()
                attachmentContent = null
                attachmentUri = null
                attachmentFileName = null
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Import from Email") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(Modifier.padding(padding).padding(16.dp)) {
            OutlinedTextField(
                value = emailText,
                onValueChange = { emailText = it },
                label = { Text("Paste booking email body") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 8
            )
            Spacer(Modifier.height(16.dp))

            // Attachment Picker Button
            Button(
                onClick = { pickFileLauncher.launch("*/*") }, // Allows picking any file type
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.AttachFile, contentDescription = "Attach File")
                Spacer(Modifier.width(8.dp))
                Text(attachmentFileName ?: "Attach Booking File (PDF/Text)")
            }

            if (attachmentFileName != null) {
                Spacer(Modifier.height(8.dp))
                Text("Attached: ${attachmentFileName}", style = MaterialTheme.typography.bodySmall)
            }

            Spacer(Modifier.height(16.dp))

            Button(
                onClick = {
                    tripViewModel.importBookingFromEmail(
                        emailContent = emailText,
                        attachmentContent = attachmentContent,
                        attachmentUri = attachmentUri?.toString()
                    )
                    navController.popBackStack()
                },
                enabled = emailText.isNotBlank() || attachmentContent != null,
                modifier = Modifier.fillMaxWidth()
            ) { Text("Import Booking") }
        }
    }
}
