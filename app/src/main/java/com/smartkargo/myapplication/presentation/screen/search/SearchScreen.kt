package com.smartkargo.myapplication.presentation.screen.search

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.smartkargo.myapplication.domain.model.Category
import com.smartkargo.myapplication.domain.model.TransactionType
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    onBack: () -> Unit,
    viewModel: SearchViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Search") },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back") } }
            )
        }
    ) { padding ->
        Column(Modifier.fillMaxSize().padding(padding).padding(16.dp)) {
            OutlinedTextField(
                value = uiState.query,
                onValueChange = viewModel::updateQuery,
                label = { Text("Search transactions") },
                leadingIcon = { Icon(Icons.Default.Search, null) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            Spacer(Modifier.height(8.dp))
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                item {
                    FilterChip(
                        selected = uiState.selectedCategory == null,
                        onClick = { viewModel.updateCategory(null) },
                        label = { Text("All") }
                    )
                }
                items(Category.entries.toList()) { cat ->
                    FilterChip(
                        selected = uiState.selectedCategory == cat.displayName,
                        onClick = { viewModel.updateCategory(cat.displayName) },
                        label = { Text(cat.displayName) }
                    )
                }
            }
            Spacer(Modifier.height(8.dp))
            val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(uiState.results, key = { it.id }) { t ->
                    Card(Modifier.fillMaxWidth()) {
                        Row(Modifier.padding(12.dp)) {
                            Column(Modifier.weight(1f)) {
                                Text(t.category, style = MaterialTheme.typography.titleSmall)
                                Text(t.note, style = MaterialTheme.typography.bodySmall)
                                Text(dateFormat.format(Date(t.date)), style = MaterialTheme.typography.labelSmall)
                            }
                            Text(
                                "${if (t.type == TransactionType.INCOME) "+" else "-"}${t.amount}",
                                color = if (t.type == TransactionType.INCOME) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }
            }
        }
    }
}

