package com.smartkargo.myapplication.domain.usecase

import com.smartkargo.myapplication.domain.model.Transaction
import com.smartkargo.myapplication.domain.model.TransactionType
import com.smartkargo.myapplication.domain.repository.TransactionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ExportTransactionsUseCase @Inject constructor(
    private val repository: TransactionRepository
) {
    operator fun invoke(startDate: Long, endDate: Long): Flow<String> {
        return repository.getTransactionsByDateRange(startDate, endDate).map { transactions ->
            buildCsv(transactions)
        }
    }

    private fun buildCsv(transactions: List<Transaction>): String {
        val sb = StringBuilder()
        sb.appendLine("ID,Amount,Type,Category,Note,Date")
        transactions.forEach { t ->
            sb.appendLine("${t.id},${t.amount},${t.type.name},${t.category},\"${t.note}\",${t.date}")
        }
        return sb.toString()
    }
}

