package com.smartkargo.myapplication.presentation.screen.alert

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.smartkargo.myapplication.domain.model.ExpenseAlert
import com.smartkargo.myapplication.domain.model.RepeatType
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlertScreen(
    onBack: () -> Unit,
    viewModel: AlertViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var showAddDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Expense Alerts") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "Add Alert")
            }
        }
    ) { padding ->
        if (uiState.isLoading) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Due Today Section
                if (uiState.todayAlerts.isNotEmpty()) {
                    item {
                        Spacer(Modifier.height(8.dp))
                        Text(
                            "⚠️ Payment Due Today",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                    items(uiState.todayAlerts, key = { it.id }) { alert ->
                        AlertCard(
                            alert = alert,
                            isUrgent = true,
                            onToggle = { viewModel.toggleAlert(alert) },
                            onDelete = { viewModel.deleteAlert(alert.id) }
                        )
                    }
                }

                // Upcoming Section
                if (uiState.upcomingAlerts.isNotEmpty()) {
                    item {
                        Spacer(Modifier.height(16.dp))
                        Text(
                            "📅 Upcoming (Next 7 Days)",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    items(uiState.upcomingAlerts, key = { it.id }) { alert ->
                        AlertCard(
                            alert = alert,
                            isUrgent = false,
                            onToggle = { viewModel.toggleAlert(alert) },
                            onDelete = { viewModel.deleteAlert(alert.id) }
                        )
                    }
                }

                // All Alerts Section
                item {
                    Spacer(Modifier.height(16.dp))
                    Text(
                        "All Alerts",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
                if (uiState.allAlerts.isEmpty()) {
                    item {
                        Text(
                            "No alerts set. Tap + to add one.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                } else {
                    items(uiState.allAlerts, key = { it.id }) { alert ->
                        AlertCard(
                            alert = alert,
                            isUrgent = false,
                            onToggle = { viewModel.toggleAlert(alert) },
                            onDelete = { viewModel.deleteAlert(alert.id) }
                        )
                    }
                }

                item { Spacer(Modifier.height(80.dp)) }
            }
        }
    }

    if (showAddDialog) {
        AddAlertDialog(
            viewModel = viewModel,
            onDismiss = {
                showAddDialog = false
                viewModel.resetForm()
            }
        )
    }
}

@Composable
fun AlertCard(
    alert: ExpenseAlert,
    isUrgent: Boolean,
    onToggle: () -> Unit,
    onDelete: () -> Unit
) {
    val dateFormat = remember { SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = if (isUrgent) CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.errorContainer
        ) else CardDefaults.cardColors()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = alert.title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "$${String.format("%.2f", alert.amount)}",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "Due: ${dateFormat.format(Date(alert.dueDate))}",
                    style = MaterialTheme.typography.bodySmall
                )
                if (alert.repeatType != RepeatType.NONE) {
                    Text(
                        text = "🔁 ${alert.repeatType.displayName}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Switch(
                    checked = alert.isEnabled,
                    onCheckedChange = { onToggle() }
                )
                IconButton(onClick = onDelete) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "Delete",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddAlertDialog(
    viewModel: AlertViewModel,
    onDismiss: () -> Unit
) {
    val formState by viewModel.formState.collectAsStateWithLifecycle()
    val dateFormat = remember { SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()) }
    var showDatePicker by remember { mutableStateOf(false) }
    var expandedRepeat by remember { mutableStateOf(false) }

    LaunchedEffect(formState.isSaved) {
        if (formState.isSaved) onDismiss()
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Expense Alert") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = formState.title,
                    onValueChange = viewModel::updateTitle,
                    label = { Text("Title (e.g. Rent, Netflix)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = formState.amount,
                    onValueChange = viewModel::updateAmount,
                    label = { Text("Amount") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                // Due Date
                OutlinedTextField(
                    value = dateFormat.format(Date(formState.dueDate)),
                    onValueChange = {},
                    label = { Text("Due Date") },
                    readOnly = true,
                    trailingIcon = {
                        IconButton(onClick = { showDatePicker = true }) {
                            Icon(Icons.Default.DateRange, contentDescription = "Pick date")
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                // Repeat Type
                ExposedDropdownMenuBox(
                    expanded = expandedRepeat,
                    onExpandedChange = { expandedRepeat = it }
                ) {
                    OutlinedTextField(
                        value = formState.repeatType.displayName,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Repeat") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedRepeat) },
                        modifier = Modifier.fillMaxWidth().menuAnchor()
                    )
                    ExposedDropdownMenu(
                        expanded = expandedRepeat,
                        onDismissRequest = { expandedRepeat = false }
                    ) {
                        RepeatType.entries.forEach { type ->
                            DropdownMenuItem(
                                text = { Text(type.displayName) },
                                onClick = {
                                    viewModel.updateRepeatType(type)
                                    expandedRepeat = false
                                }
                            )
                        }
                    }
                }

                formState.error?.let {
                    Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
                }
            }
        },
        confirmButton = {
            Button(onClick = viewModel::saveAlert) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )

    if (showDatePicker) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = formState.dueDate
        )
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { viewModel.updateDueDate(it) }
                    showDatePicker = false
                }) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) { Text("Cancel") }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}

