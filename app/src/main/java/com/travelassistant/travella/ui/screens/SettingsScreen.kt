// app/src/main/java/com/travelassistant/travella/ui/screens/SettingsScreen.kt
package com.travelassistant.travella.ui.screens

import android.content.Intent
import android.net.Uri
import android.provider.Settings
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.travelassistant.travella.viewmodel.SettingsViewModel
import kotlinx.coroutines.launch

// NEW: export manager + databases
import com.travelassistant.travella.export.ExportDataManager
import com.travelassistant.travella.export.ExportOptions
import com.travelassistant.travella.data.database.TripDatabase
import com.travelassistant.travella.data.database.ExpenseDatabase

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    navController: NavHostController,
    viewModel: SettingsViewModel
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    // App state
    val isDarkMode by viewModel.isDarkMode.collectAsState()
    val notificationsEnabled by viewModel.notificationsEnabled.collectAsState()
    val locationEnabled by viewModel.locationEnabled.collectAsState()
    val autoSyncEnabled by viewModel.autoSyncEnabled.collectAsState()
    val biometricEnabled by viewModel.biometricEnabled.collectAsState()
    val offlineModeEnabled by viewModel.offlineModeEnabled.collectAsState()

    // Dialog state
    var showAboutDialog by remember { mutableStateOf(false) }
    var showResetDialog by remember { mutableStateOf(false) }

    // Export dialog state
    var showExportDialog by remember { mutableStateOf(false) }
    var exportTrips by remember { mutableStateOf(true) }
    var exportExpenses by remember { mutableStateOf(true) }
    var exporting by remember { mutableStateOf(false) }
    var exportedUri by remember { mutableStateOf<Uri?>(null) }

    // Auto-open exported PDF if possible
    LaunchedEffect(exportedUri) {
        exportedUri?.let { uri ->
            try {
                val intent = Intent(Intent.ACTION_VIEW).apply {
                    setDataAndType(uri, "application/pdf")
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                }
                context.startActivity(intent)
            } catch (_: Exception) {
                // No viewer installed; ignore
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            "Settings",
                            style = MaterialTheme.typography.headlineSmall.copy(
                                fontWeight = FontWeight.Bold
                            )
                        )
                        Text(
                            "Customize your experience",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Theme & Display (Language/Currency removed)
            item {
                SettingsSection(
                    title = "Theme & Display",
                    icon = Icons.Default.Palette
                ) {
                    SettingsSwitchItem(
                        title = "Dark Mode",
                        subtitle = if (isDarkMode) "Dark theme enabled" else "Light theme enabled",
                        icon = if (isDarkMode) Icons.Default.DarkMode else Icons.Default.LightMode,
                        checked = isDarkMode,
                        onCheckedChange = { viewModel.toggleDarkMode(it) }
                    )
                }
            }

            // Privacy & Permissions
            item {
                SettingsSection(
                    title = "Privacy & Permissions",
                    icon = Icons.Default.Security
                ) {
                    SettingsSwitchItem(
                        title = "Location Services",
                        subtitle = if (locationEnabled) "Allow location access" else "Location access disabled",
                        icon = Icons.Default.LocationOn,
                        checked = locationEnabled,
                        onCheckedChange = { viewModel.toggleLocation(it) }
                    )

                    SettingsSwitchItem(
                        title = "Notifications",
                        subtitle = if (notificationsEnabled) "Receive push notifications" else "Notifications disabled",
                        icon = Icons.Default.Notifications,
                        checked = notificationsEnabled,
                        onCheckedChange = { viewModel.toggleNotifications(it) }
                    )

                    SettingsClickableItem(
                        title = "App Permissions",
                        subtitle = "Manage system permissions",
                        icon = Icons.Default.Shield,
                        onClick = {
                            try {
                                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                                intent.data = Uri.fromParts("package", context.packageName, null)
                                context.startActivity(intent)
                            } catch (e: Exception) {
                                Toast.makeText(context, "Unable to open settings", Toast.LENGTH_SHORT).show()
                            }
                        }
                    )

                    SettingsSwitchItem(
                        title = "Biometric Authentication",
                        subtitle = if (biometricEnabled) "Use fingerprint/face unlock" else "Disabled",
                        icon = Icons.Default.Fingerprint,
                        checked = biometricEnabled,
                        onCheckedChange = { viewModel.toggleBiometric(it) }
                    )
                }
            }

            // Data & Sync
            item {
                SettingsSection(
                    title = "Data & Sync",
                    icon = Icons.Default.CloudSync
                ) {
                    SettingsSwitchItem(
                        title = "Auto-Sync",
                        subtitle = if (autoSyncEnabled) "Automatically sync data" else "Manual sync only",
                        icon = Icons.Default.Sync,
                        checked = autoSyncEnabled,
                        onCheckedChange = { viewModel.toggleAutoSync(it) }
                    )

                    SettingsSwitchItem(
                        title = "Offline Mode",
                        subtitle = if (offlineModeEnabled) "Work without internet" else "Online mode",
                        icon = Icons.Default.CloudOff,
                        checked = offlineModeEnabled,
                        onCheckedChange = { viewModel.toggleOfflineMode(it) }
                    )

                    // REPLACED: used to call viewModel.exportData(); now opens options dialog
                    SettingsClickableItem(
                        title = "Export Data",
                        subtitle = "PDF of Trips and/or Expenses to Downloads",
                        icon = Icons.Default.GetApp,
                        onClick = { showExportDialog = true }
                    )

                    SettingsClickableItem(
                        title = "Clear Cache",
                        subtitle = "Free up storage space",
                        icon = Icons.Default.CleaningServices,
                        onClick = {
                            viewModel.clearCache()
                            Toast.makeText(context, "Cache cleared", Toast.LENGTH_SHORT).show()
                        }
                    )
                }
            }

            // Support & Info
            item {
                SettingsSection(
                    title = "Support & Info",
                    icon = Icons.Default.Info
                ) {
                    SettingsClickableItem(
                        title = "Help & FAQ",
                        subtitle = "Get help with using the app",
                        icon = Icons.Default.Help,
                        onClick = { navController.navigate("help") }
                    )

                    SettingsClickableItem(
                        title = "Contact Support",
                        subtitle = "Get in touch with our team",
                        icon = Icons.Default.ContactSupport,
                        onClick = {
                            val intent = Intent(Intent.ACTION_SENDTO).apply {
                                data = Uri.parse("mailto:support@travella.com")
                                putExtra(Intent.EXTRA_SUBJECT, "Travella App Support")
                            }
                            try {
                                context.startActivity(intent)
                            } catch (e: Exception) {
                                Toast.makeText(context, "No email app found", Toast.LENGTH_SHORT).show()
                            }
                        }
                    )

                    SettingsClickableItem(
                        title = "Rate the App",
                        subtitle = "Share your feedback",
                        icon = Icons.Default.Star,
                        onClick = {
                            try {
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=${context.packageName}"))
                                context.startActivity(intent)
                            } catch (e: Exception) {
                                val intent = Intent(
                                    Intent.ACTION_VIEW,
                                    Uri.parse("https://play.google.com/store/apps/details?id=${context.packageName}")
                                )
                                context.startActivity(intent)
                            }
                        }
                    )

                    SettingsClickableItem(
                        title = "About",
                        subtitle = "Version ${viewModel.getAppVersion()}",
                        icon = Icons.Default.Info,
                        onClick = { showAboutDialog = true }
                    )
                }
            }

            // Advanced
            item {
                SettingsSection(
                    title = "Advanced",
                    icon = Icons.Default.Settings
                ) {
                    SettingsClickableItem(
                        title = "Reset Settings",
                        subtitle = "Restore default settings",
                        icon = Icons.Default.RestoreFromTrash,
                        onClick = { showResetDialog = true },
                        isDestructive = true
                    )
                }
            }

            item { Spacer(modifier = Modifier.height(32.dp)) }
        }
    }

    // Export dialog
    if (showExportDialog) {
        AlertDialog(
            onDismissRequest = { if (!exporting) showExportDialog = false },
            title = { Text("Export to PDF") },
            text = {
                Column {
                    Text("Choose what to include")
                    Spacer(Modifier.height(8.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(checked = exportTrips, onCheckedChange = { exportTrips = it })
                        Spacer(Modifier.width(8.dp))
                        Text("Trips")
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(checked = exportExpenses, onCheckedChange = { exportExpenses = it })
                        Spacer(Modifier.width(8.dp))
                        Text("Expenses")
                    }
                    if (exporting) {
                        Spacer(Modifier.height(12.dp))
                        LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                        Text(
                            "Generating PDF…",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                        )
                    }
                }
            },
            confirmButton = {
                TextButton(
                    enabled = !exporting && (exportTrips || exportExpenses),
                    onClick = {
                        exporting = true
                        exportedUri = null
                        scope.launch {
                            try {
                                val tripDao = TripDatabase.get(context).tripDao() // DB accessor
                                val expenseDao = ExpenseDatabase.getDatabase(context).expenseDao() // DB accessor
                                val exporter = ExportDataManager(context, tripDao, expenseDao)
                                val uri = exporter.exportToPdf(
                                    ExportOptions(includeTrips = exportTrips, includeExpenses = exportExpenses)
                                )
                                exporting = false
                                if (uri != null) {
                                    exportedUri = uri
                                    Toast.makeText(context, "Saved to Downloads", Toast.LENGTH_SHORT).show()
                                    showExportDialog = false
                                } else {
                                    Toast.makeText(context, "Failed to export", Toast.LENGTH_SHORT).show()
                                }
                            } catch (e: Exception) {
                                exporting = false
                                Toast.makeText(context, "Export failed: ${e.message}", Toast.LENGTH_LONG).show()
                            }
                        }
                    }
                ) { Text("Export") }
            },
            dismissButton = {
                TextButton(enabled = !exporting, onClick = { showExportDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // About dialog
    if (showAboutDialog) {
        AboutDialog(
            appVersion = viewModel.getAppVersion(),
            buildNumber = viewModel.getBuildNumber(),
            onDismiss = { showAboutDialog = false }
        )
    }

    // Reset confirmation
    if (showResetDialog) {
        AlertDialog(
            onDismissRequest = { showResetDialog = false },
            title = { Text("Reset Settings") },
            text = { Text("Are you sure you want to reset all settings to default? This action cannot be undone.") },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.resetSettings()
                        showResetDialog = false
                        Toast.makeText(context, "Settings reset to default", Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    )
                ) { Text("Reset") }
            },
            dismissButton = {
                OutlinedButton(onClick = { showResetDialog = false }) { Text("Cancel") }
            }
        )
    }
}

/* ------- Helpers ------- */

@Composable
private fun SettingsSection(
    title: String,
    icon: ImageVector,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 16.dp)
            ) {
                Icon(
                    icon,
                    contentDescription = null,
                    modifier = Modifier.size(24.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            content()
        }
    }
}

@Composable
private fun SettingsSwitchItem(
    title: String,
    subtitle: String,
    icon: ImageVector,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            icon,
            contentDescription = null,
            modifier = Modifier.size(24.dp),
            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(title, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium)
            Text(
                subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
        }
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}

@Composable
private fun SettingsClickableItem(
    title: String,
    subtitle: String,
    icon: ImageVector,
    onClick: () -> Unit,
    isDestructive: Boolean = false
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            icon,
            contentDescription = null,
            modifier = Modifier.size(24.dp),
            tint = if (isDestructive) MaterialTheme.colorScheme.error
            else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                title,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium,
                color = if (isDestructive) MaterialTheme.colorScheme.error
                else MaterialTheme.colorScheme.onSurface
            )
            Text(
                subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = if (isDestructive) MaterialTheme.colorScheme.error.copy(alpha = 0.7f)
                else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
        }
        Icon(
            Icons.Default.ChevronRight,
            contentDescription = null,
            modifier = Modifier.size(20.dp),
            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
        )
    }
}

@Composable
private fun AboutDialog(
    appVersion: String,
    buildNumber: String,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Default.Flight,
                    contentDescription = null,
                    modifier = Modifier.size(32.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Travella")
            }
        },
        text = {
            Column {
                Text("Version: $appVersion")
                Text("Build: $buildNumber")
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    "Your ultimate travel companion for planning, organizing, and tracking your adventures.",
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    "© 2025 Travella Inc. All rights reserved.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            }
        },
        confirmButton = { TextButton(onClick = onDismiss) { Text("OK") } }
    )
}
