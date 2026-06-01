package com.smartkargo.myapplication.presentation.screen.calendar

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smartkargo.myapplication.domain.model.Transaction
import com.smartkargo.myapplication.domain.usecase.GetTransactionsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

data class CalendarUiState(
    val allTransactions: List<Transaction> = emptyList(),
    val selectedDate: Long = System.currentTimeMillis(),
    val transactionsForSelectedDate: List<Transaction> = emptyList(),
    val isLoading: Boolean = true
)

@HiltViewModel
class CalendarViewModel @Inject constructor(
    private val getTransactions: GetTransactionsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(CalendarUiState())
    val uiState: StateFlow<CalendarUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            getTransactions().collect { transactions ->
                val current = _uiState.value
                _uiState.value = current.copy(
                    allTransactions = transactions,
                    transactionsForSelectedDate = filterByDate(transactions, current.selectedDate),
                    isLoading = false
                )
            }
        }
    }

    fun selectDate(date: Long) {
        val transactions = _uiState.value.allTransactions
        _uiState.update {
            it.copy(
                selectedDate = date,
                transactionsForSelectedDate = filterByDate(transactions, date)
            )
        }
    }

    private fun filterByDate(transactions: List<Transaction>, date: Long): List<Transaction> {
        val cal = Calendar.getInstance().apply {
            timeInMillis = date
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val startOfDay = cal.timeInMillis
        cal.add(Calendar.DAY_OF_YEAR, 1)
        val endOfDay = cal.timeInMillis
        return transactions.filter { it.date in startOfDay until endOfDay }
    }

    fun getTransactionDates(): Set<Long> {
        return _uiState.value.allTransactions.map { tx ->
            val cal = Calendar.getInstance().apply {
                timeInMillis = tx.date
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }
            cal.timeInMillis
        }.toSet()
    }
}
