package com.smartkargo.myapplication.domain.usecase

import com.smartkargo.myapplication.domain.model.Transaction
import com.smartkargo.myapplication.domain.repository.TransactionRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetTransactionsUseCase @Inject constructor(
    private val repository: TransactionRepository
) {
    operator fun invoke(): Flow<List<Transaction>> = repository.getAllTransactions()

    fun byDateRange(startDate: Long, endDate: Long): Flow<List<Transaction>> =
        repository.getTransactionsByDateRange(startDate, endDate)

    fun byCategory(category: String): Flow<List<Transaction>> =
        repository.getTransactionsByCategory(category)

    fun search(query: String): Flow<List<Transaction>> =
        repository.searchTransactions(query)
}

