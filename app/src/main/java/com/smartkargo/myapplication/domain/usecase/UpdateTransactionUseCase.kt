package com.smartkargo.myapplication.domain.usecase

import com.smartkargo.myapplication.domain.model.Transaction
import com.smartkargo.myapplication.domain.repository.TransactionRepository
import javax.inject.Inject

class UpdateTransactionUseCase @Inject constructor(
    private val repository: TransactionRepository
) {
    suspend operator fun invoke(transaction: Transaction) =
        repository.updateTransaction(transaction)
}

