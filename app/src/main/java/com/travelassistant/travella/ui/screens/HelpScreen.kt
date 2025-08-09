package com.travelassistant.travella.ui.screens

import android.content.Intent
import android.net.Uri
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
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HelpScreen(navController: NavHostController) {
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            "Help & Support",
                            style = MaterialTheme.typography.headlineSmall.copy(
                                fontWeight = FontWeight.Bold
                            )
                        )
                        Text(
                            "Get help and support",
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
            // Quick Start Guide
            item {
                HelpSection(
                    title = "Quick Start Guide",
                    icon = Icons.Default.PlayArrow
                ) {
                    HelpItem(
                        title = "Create Your First Trip",
                        description = "Tap the '+' button on the dashboard to add a new trip with destination and dates."
                    )
                    HelpItem(
                        title = "Add Expenses",
                        description = "Go to Expenses tab and track your spending with categories and receipts."
                    )
                    HelpItem(
                        title = "Set Reminders",
                        description = "Never miss important travel tasks with our smart reminder system."
                    )
                    HelpItem(
                        title = "Use Maps",
                        description = "Find locations and navigate to your destinations with integrated maps."
                    )
                }
            }

            // FAQ Section
            item {
                HelpSection(
                    title = "Frequently Asked Questions",
                    icon = Icons.Default.QuestionAnswer
                ) {
                    ExpandableHelpItem(
                        question = "How do I sync my data across devices?",
                        answer = "Enable Auto-Sync in Settings > Data & Sync. Your data will automatically sync when connected to the internet."
                    )
                    ExpandableHelpItem(
                        question = "Can I use the app offline?",
                        answer = "Yes! Enable Offline Mode in Settings. Your trips and expenses will be saved locally and sync when you're back online."
                    )
                    ExpandableHelpItem(
                        question = "How do I change the app language?",
                        answer = "Go to Settings > Theme & Display > Language and select your preferred language from the list."
                    )
                    ExpandableHelpItem(
                        question = "Is my data secure?",
                        answer = "Absolutely! We use industry-standard encryption and never share your personal data with third parties."
                    )
                    ExpandableHelpItem(
                        question = "How do I export my travel data?",
                        answer = "Go to Settings > Data & Sync > Export Data to backup your trips, expenses, and itineraries."
                    )
                }
            }

            // Features Guide
            item {
                HelpSection(
                    title = "App Features",
                    icon = Icons.Default.Star
                ) {
                    HelpItem(
                        title = "Trip Planning",
                        description = "Organize your trips with dates, budgets, and detailed itineraries."
                    )
                    HelpItem(
                        title = "Expense Tracking",
                        description = "Track spending by category with receipt photos and currency conversion."
                    )
                    HelpItem(
                        title = "Smart Reminders",
                        description = "Get notified about important travel tasks and deadlines."
                    )
                    HelpItem(
                        title = "Collaboration",
                        description = "Share trips with travel companions and plan together in real-time."
                    )
                }
            }

            // Contact Support
            item {
                HelpSection(
                    title = "Contact Support",
                    icon = Icons.Default.ContactSupport
                ) {
                    SupportContactItem(
                        title = "Email Support",
                        subtitle = "support@travella.com",
                        icon = Icons.Default.Email,
                        onClick = {
                            val intent = Intent(Intent.ACTION_SENDTO).apply {
                                data = Uri.parse("mailto:support@travella.com")
                                putExtra(Intent.EXTRA_SUBJECT, "Travella App Support")
                                putExtra(Intent.EXTRA_TEXT, "Hi Travella Team,\n\nI need help with...\n\nDevice: Android\nApp Version: 1.0.0\n\n")
                            }
                            try {
                                context.startActivity(intent)
                            } catch (e: Exception) {
                                Toast.makeText(context, "No email app found", Toast.LENGTH_SHORT).show()
                            }
                        }
                    )

                    SupportContactItem(
                        title = "Report a Bug",
                        subtitle = "Help us improve the app",
                        icon = Icons.Default.BugReport,
                        onClick = {
                            val intent = Intent(Intent.ACTION_SENDTO).apply {
                                data = Uri.parse("mailto:bugs@travella.com")
                                putExtra(Intent.EXTRA_SUBJECT, "Bug Report - Travella App")
                                putExtra(Intent.EXTRA_TEXT, "Bug Description:\n\nSteps to Reproduce:\n1. \n2. \n3. \n\nExpected Result:\n\nActual Result:\n\nDevice Info:\nAndroid Version: ${android.os.Build.VERSION.RELEASE}\nApp Version: 1.0.0\n")
                            }
                            try {
                                context.startActivity(intent)
                            } catch (e: Exception) {
                                Toast.makeText(context, "No email app found", Toast.LENGTH_SHORT).show()
                            }
                        }
                    )

                    SupportContactItem(
                        title = "Feature Request",
                        subtitle = "Suggest new features",
                        icon = Icons.Default.Lightbulb,
                        onClick = {
                            val intent = Intent(Intent.ACTION_SENDTO).apply {
                                data = Uri.parse("mailto:feedback@travella.com")
                                putExtra(Intent.EXTRA_SUBJECT, "Feature Request - Travella App")
                                putExtra(Intent.EXTRA_TEXT, "Feature Request:\n\nWhy this feature would be useful:\n\nHow it should work:\n\n")
                            }
                            try {
                                context.startActivity(intent)
                            } catch (e: Exception) {
                                Toast.makeText(context, "No email app found", Toast.LENGTH_SHORT).show()
                            }
                        }
                    )
                }
            }

            // App Information
            item {
                HelpSection(
                    title = "App Information",
                    icon = Icons.Default.Info
                ) {
                    HelpItem(
                        title = "Version",
                        description = "1.0.0 (Build 1)"
                    )
                    HelpItem(
                        title = "Developer",
                        description = "Travella Inc."
                    )
                    HelpItem(
                        title = "Privacy Policy",
                        description = "View our privacy policy and terms of service.",
                        isClickable = true,
                        onClick = {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://travella.com/privacy"))
                            try {
                                context.startActivity(intent)
                            } catch (e: Exception) {
                                Toast.makeText(context, "Unable to open link", Toast.LENGTH_SHORT).show()
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun HelpSection(
    title: String,
    icon: ImageVector,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
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
private fun HelpItem(
    title: String,
    description: String,
    isClickable: Boolean = false,
    onClick: (() -> Unit)? = null
) {
    val modifier = if (isClickable && onClick != null) {
        Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 8.dp)
    } else {
        Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    }

    Column(modifier = modifier) {
        Text(
            title,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Medium,
            color = if (isClickable) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            description,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
        )
    }
}

@Composable
private fun ExpandableHelpItem(
    question: String,
    answer: String
) {
    var expanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { expanded = !expanded }
            .padding(vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                question,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.weight(1f)
            )
            Icon(
                if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                contentDescription = null,
                modifier = Modifier.size(20.dp)
            )
        }

        if (expanded) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                answer,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
        }
    }
}

@Composable
private fun SupportContactItem(
    title: String,
    subtitle: String,
    icon: ImageVector,
    onClick: () -> Unit
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
            tint = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                title,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium
            )
            Text(
                subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
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