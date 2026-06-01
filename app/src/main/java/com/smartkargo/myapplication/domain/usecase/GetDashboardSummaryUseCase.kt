package com.smartkargo.myapplication.domain.usecase

import com.smartkargo.myapplication.domain.model.DashboardSummary
import com.smartkargo.myapplication.domain.repository.TransactionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

class GetDashboardSummaryUseCase @Inject constructor(
    private val repository: TransactionRepository
) {
    operator fun invoke(startDate: Long, endDate: Long): Flow<DashboardSummary> {
        return combine(
            repository.getTotalIncome(startDate, endDate),
            repository.getTotalExpense(startDate, endDate)
        ) { income, expense ->
            DashboardSummary(
                totalIncome = income,
                totalExpense = expense,
                savings = income - expense
            )
        }
    }
}

