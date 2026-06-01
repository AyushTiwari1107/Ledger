package com.smartkargo.myapplication.presentation.screen.analytics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smartkargo.myapplication.domain.model.CategoryBreakdown
import com.smartkargo.myapplication.domain.model.DashboardSummary
import com.smartkargo.myapplication.domain.usecase.GetCategoryBreakdownUseCase
import com.smartkargo.myapplication.domain.usecase.GetDashboardSummaryUseCase
import com.smartkargo.myapplication.domain.usecase.GetTransactionsUseCase
import com.smartkargo.myapplication.domain.model.TransactionType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

data class MonthlyData(
    val label: String,       // e.g. "Jan", "Feb"
    val expense: Double,
    val income: Double
)

data class AnalyticsUiState(
    val weeklySummary: DashboardSummary = DashboardSummary(),
    val monthlySummary: DashboardSummary = DashboardSummary(),
    val categoryBreakdown: List<CategoryBreakdown> = emptyList(),
    val monthlyData: List<MonthlyData> = emptyList(),
    val isLoading: Boolean = true
)

@HiltViewModel
class AnalyticsViewModel @Inject constructor(
    private val getDashboardSummary: GetDashboardSummaryUseCase,
    private val getCategoryBreakdown: GetCategoryBreakdownUseCase,
    private val getTransactions: GetTransactionsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(AnalyticsUiState())
    val uiState: StateFlow<AnalyticsUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            val cal = Calendar.getInstance()
            val now = cal.timeInMillis

            // Weekly range
            cal.add(Calendar.DAY_OF_YEAR, -7)
            val weekStart = cal.timeInMillis

            // Current month range
            cal.timeInMillis = now
            cal.set(Calendar.DAY_OF_MONTH, 1)
            cal.set(Calendar.HOUR_OF_DAY, 0)
            cal.set(Calendar.MINUTE, 0)
            cal.set(Calendar.SECOND, 0)
            val monthStart = cal.timeInMillis
            cal.add(Calendar.MONTH, 1)
            val monthEnd = cal.timeInMillis

            // Build 6-month ranges
            val monthRanges = mutableListOf<Triple<String, Long, Long>>()
            val monthFmt = SimpleDateFormat("MMM", Locale.getDefault())
            val tmp = Calendar.getInstance()
            tmp.set(Calendar.DAY_OF_MONTH, 1)
            tmp.set(Calendar.HOUR_OF_DAY, 0)
            tmp.set(Calendar.MINUTE, 0)
            tmp.set(Calendar.SECOND, 0)
            tmp.set(Calendar.MILLISECOND, 0)
            repeat(6) {
                val start = tmp.timeInMillis
                val label = monthFmt.format(tmp.time)
                tmp.add(Calendar.MONTH, 1)
                val end = tmp.timeInMillis
                monthRanges.add(0, Triple(label, start, end))
                tmp.add(Calendar.MONTH, -2)
                tmp.set(Calendar.DAY_OF_MONTH, 1)
                tmp.add(Calendar.MONTH, 1)
            }

            combine(
                getDashboardSummary(weekStart, now),
                getDashboardSummary(monthStart, monthEnd),
                getCategoryBreakdown(monthStart, monthEnd),
                getTransactions()
            ) { weekly, monthly, breakdown, allTx ->
                // Compute monthly income/expense
                val monthlyData = monthRanges.map { (label, start, end) ->
                    val expense = allTx.filter { it.type == TransactionType.EXPENSE && it.date in start until end }.sumOf { it.amount }
                    val income = allTx.filter { it.type == TransactionType.INCOME && it.date in start until end }.sumOf { it.amount }
                    MonthlyData(label, expense, income)
                }
                AnalyticsUiState(
                    weeklySummary = weekly,
                    monthlySummary = monthly,
                    categoryBreakdown = breakdown,
                    monthlyData = monthlyData,
                    isLoading = false
                )
            }.collect { _uiState.value = it }
        }
    }
}
