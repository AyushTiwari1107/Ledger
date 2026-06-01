package com.smartkargo.myapplication.domain.usecase

import com.smartkargo.myapplication.domain.model.CategoryBreakdown
import com.smartkargo.myapplication.domain.model.TransactionType
import com.smartkargo.myapplication.domain.repository.TransactionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetCategoryBreakdownUseCase @Inject constructor(
    private val repository: TransactionRepository
) {
    operator fun invoke(startDate: Long, endDate: Long): Flow<List<CategoryBreakdown>> {
        return repository.getTransactionsByDateRange(startDate, endDate).map { transactions ->
            val expenses = transactions.filter { it.type == TransactionType.EXPENSE }
            val total = expenses.sumOf { it.amount }
            if (total == 0.0) return@map emptyList()
            expenses.groupBy { it.category }
                .map { (category, items) ->
                    val amount = items.sumOf { it.amount }
                    CategoryBreakdown(
                        category = category,
                        amount = amount,
                        percentage = (amount / total * 100).toFloat()
                    )
                }
                .sortedByDescending { it.amount }
        }
    }
}

