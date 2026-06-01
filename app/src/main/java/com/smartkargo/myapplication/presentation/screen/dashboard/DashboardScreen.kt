package com.smartkargo.myapplication.presentation.screen.dashboard

import androidx.compose.foundation.clickable
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
import com.smartkargo.myapplication.domain.model.Transaction
import com.smartkargo.myapplication.domain.model.TransactionType
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    onAddTransaction: () -> Unit,
    onEditTransaction: (Long) -> Unit,
    onAnalytics: () -> Unit,
    onSearch: () -> Unit,
    onSettings: () -> Unit,
    onExport: () -> Unit,
    onAlerts: () -> Unit = {},
    onCalendar: () -> Unit = {},
    viewModel: DashboardViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Ledger") },
                actions = {
                    IconButton(onClick = onSearch) { Icon(Icons.Default.Search, "Search") }
                    IconButton(onClick = onCalendar) { Icon(Icons.Default.DateRange, "Calendar") }
                    IconButton(onClick = onAlerts) { Icon(Icons.Default.Notifications, "Alerts") }
                    IconButton(onClick = onAnalytics) { Icon(Icons.Default.Analytics, "Analytics") }
                    IconButton(onClick = onExport) { Icon(Icons.Default.FileDownload, "Export") }
                    IconButton(onClick = onSettings) { Icon(Icons.Default.Settings, "Settings") }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddTransaction) {
                Icon(Icons.Default.Add, "Add Transaction")
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
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item { Spacer(Modifier.height(8.dp)) }
                item { SummaryCards(uiState) }
                item {
                    Text(
                        "Recent Transactions",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
                items(uiState.recentTransactions, key = { it.id }) { transaction ->
                    TransactionItem(
                        transaction = transaction,
                        currency = uiState.currency,
                        onClick = { onEditTransaction(transaction.id) },
                        onDelete = { viewModel.deleteTransaction(transaction.id) }
                    )
                }
                item { Spacer(Modifier.height(80.dp)) }
            }
        }
    }
}

@Composable
fun SummaryCards(uiState: DashboardUiState) {
    val currency = uiState.currency
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        SummaryCard("Income", uiState.summary.totalIncome, currency, MaterialTheme.colorScheme.primary, Modifier.weight(1f))
        SummaryCard("Expense", uiState.summary.totalExpense, currency, MaterialTheme.colorScheme.error, Modifier.weight(1f))
        SummaryCard("Savings", uiState.summary.savings, currency, MaterialTheme.colorScheme.tertiary, Modifier.weight(1f))
    }
}

@Composable
fun SummaryCard(title: String, amount: Double, currency: String, color: androidx.compose.ui.graphics.Color, modifier: Modifier) {
    Card(modifier = modifier, colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.1f))) {
        Column(Modifier.padding(12.dp)) {
            Text(title, style = MaterialTheme.typography.labelSmall)
            Text("$currency %.2f".format(amount), style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = color)
        }
    }
}

@Composable
fun TransactionItem(transaction: Transaction, currency: String, onClick: () -> Unit, onDelete: () -> Unit) {
    val dateFormat = SimpleDateFormat("MMM dd", Locale.getDefault())
    Card(Modifier.fillMaxWidth().clickable { onClick() }) {
        Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text(transaction.category, fontWeight = FontWeight.Medium)
                Text(transaction.note, style = MaterialTheme.typography.bodySmall)
                Text(dateFormat.format(Date(transaction.date)), style = MaterialTheme.typography.labelSmall)
            }
            Text(
                text = "${if (transaction.type == TransactionType.INCOME) "+" else "-"}$currency %.2f".format(transaction.amount),
                color = if (transaction.type == TransactionType.INCOME) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error,
                fontWeight = FontWeight.Bold
            )
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, "Delete", tint = MaterialTheme.colorScheme.error)
            }
        }
    }
}

