package com.smartkargo.myapplication.presentation.screen.transaction

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
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
fun AddTransactionScreen(
    onBack: () -> Unit,
    viewModel: TransactionViewModel = hiltViewModel()
) {
    val formState by viewModel.formState.collectAsStateWithLifecycle()

    LaunchedEffect(formState.isSaved) { if (formState.isSaved) onBack() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Add Transaction") },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back") } }
            )
        }
    ) { padding ->
        TransactionForm(
            formState = formState,
            onAmountChange = viewModel::updateAmount,
            onCategoryChange = viewModel::updateCategory,
            onNoteChange = viewModel::updateNote,
            onTypeChange = viewModel::updateType,
            onDateChange = viewModel::updateDate,
            onSave = { viewModel.saveTransaction() },
            modifier = Modifier.padding(padding)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditTransactionScreen(
    transactionId: Long,
    onBack: () -> Unit,
    viewModel: TransactionViewModel = hiltViewModel()
) {
    val formState by viewModel.formState.collectAsStateWithLifecycle()

    LaunchedEffect(transactionId) { viewModel.loadTransaction(transactionId) }
    LaunchedEffect(formState.isSaved) { if (formState.isSaved) onBack() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Edit Transaction") },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back") } }
            )
        }
    ) { padding ->
        TransactionForm(
            formState = formState,
            onAmountChange = viewModel::updateAmount,
            onCategoryChange = viewModel::updateCategory,
            onNoteChange = viewModel::updateNote,
            onTypeChange = viewModel::updateType,
            onDateChange = viewModel::updateDate,
            onSave = { viewModel.saveTransaction(transactionId) },
            modifier = Modifier.padding(padding)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionForm(
    formState: TransactionFormState,
    onAmountChange: (String) -> Unit,
    onCategoryChange: (String) -> Unit,
    onNoteChange: (String) -> Unit,
    onTypeChange: (TransactionType) -> Unit,
    onDateChange: (Long) -> Unit,
    onSave: () -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    var showDatePicker by remember { mutableStateOf(false) }
    val dateFormat = remember { SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Type selector
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            FilterChip(
                selected = formState.type == TransactionType.EXPENSE,
                onClick = { onTypeChange(TransactionType.EXPENSE) },
                label = { Text("Expense") },
                modifier = Modifier.weight(1f)
            )
            FilterChip(
                selected = formState.type == TransactionType.INCOME,
                onClick = { onTypeChange(TransactionType.INCOME) },
                label = { Text("Income") },
                modifier = Modifier.weight(1f)
            )
        }

        OutlinedTextField(
            value = formState.amount,
            onValueChange = onAmountChange,
            label = { Text("Amount") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        // Date Picker - defaults to today
        OutlinedTextField(
            value = dateFormat.format(Date(formState.date)),
            onValueChange = {},
            readOnly = true,
            label = { Text("Date") },
            trailingIcon = {
                IconButton(onClick = { showDatePicker = true }) {
                    Icon(Icons.Default.DateRange, contentDescription = "Pick date")
                }
            },
            modifier = Modifier.fillMaxWidth()
        )

        ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = it }) {
            OutlinedTextField(
                value = formState.category,
                onValueChange = {},
                readOnly = true,
                label = { Text("Category") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
                modifier = Modifier.fillMaxWidth().menuAnchor()
            )
            ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                Category.entries.forEach { cat ->
                    DropdownMenuItem(
                        text = { Text(cat.displayName) },
                        onClick = { onCategoryChange(cat.displayName); expanded = false }
                    )
                }
            }
        }

        OutlinedTextField(
            value = formState.note,
            onValueChange = onNoteChange,
            label = { Text("Note") },
            modifier = Modifier.fillMaxWidth(),
            maxLines = 3
        )

        formState.error?.let {
            Text(it, color = MaterialTheme.colorScheme.error)
        }

        Button(onClick = onSave, modifier = Modifier.fillMaxWidth()) {
            Text("Save")
        }
    }

    if (showDatePicker) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = formState.date
        )
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { onDateChange(it) }
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
