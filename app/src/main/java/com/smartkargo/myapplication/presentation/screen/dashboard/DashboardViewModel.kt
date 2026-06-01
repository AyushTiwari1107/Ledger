package com.smartkargo.myapplication.presentation.screen.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smartkargo.myapplication.domain.model.DashboardSummary
import com.smartkargo.myapplication.domain.model.Transaction
import com.smartkargo.myapplication.domain.usecase.DeleteTransactionUseCase
import com.smartkargo.myapplication.domain.usecase.GetDashboardSummaryUseCase
import com.smartkargo.myapplication.domain.usecase.GetTransactionsUseCase
import com.smartkargo.myapplication.domain.repository.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

data class DashboardUiState(
    val summary: DashboardSummary = DashboardSummary(),
    val recentTransactions: List<Transaction> = emptyList(),
    val currency: String = "USD",
    val isLoading: Boolean = true
)

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val getDashboardSummary: GetDashboardSummaryUseCase,
    private val getTransactions: GetTransactionsUseCase,
    private val deleteTransactionUseCase: DeleteTransactionUseCase,
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        val cal = Calendar.getInstance()
        cal.set(Calendar.DAY_OF_MONTH, 1)
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        val startOfMonth = cal.timeInMillis
        cal.add(Calendar.MONTH, 1)
        val endOfMonth = cal.timeInMillis

        viewModelScope.launch {
            combine(
                getDashboardSummary(startOfMonth, endOfMonth),
                getTransactions(),
                settingsRepository.getCurrency()
            ) { summary, transactions, currency ->
                DashboardUiState(
                    summary = summary,
                    recentTransactions = transactions.take(10),
                    currency = currency,
                    isLoading = false
                )
            }.collect { _uiState.value = it }
        }
    }

    fun deleteTransaction(id: Long) {
        viewModelScope.launch { deleteTransactionUseCase(id) }
    }
}
