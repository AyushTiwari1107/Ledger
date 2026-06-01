package com.smartkargo.myapplication.domain.usecase

import com.smartkargo.myapplication.domain.model.Transaction
import com.smartkargo.myapplication.domain.repository.TransactionRepository
import javax.inject.Inject

class AddTransactionUseCase @Inject constructor(
    private val repository: TransactionRepository
) {
    suspend operator fun invoke(transaction: Transaction): Long =
        repository.insertTransaction(transaction)
}

