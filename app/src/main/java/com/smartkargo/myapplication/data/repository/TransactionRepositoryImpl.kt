package com.smartkargo.myapplication.data.repository

import com.smartkargo.myapplication.data.local.dao.TransactionDao
import com.smartkargo.myapplication.data.mapper.toDomain
import com.smartkargo.myapplication.data.mapper.toEntity
import com.smartkargo.myapplication.domain.model.Transaction
import com.smartkargo.myapplication.domain.repository.TransactionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class TransactionRepositoryImpl @Inject constructor(
    private val dao: TransactionDao
) : TransactionRepository {

    override fun getAllTransactions(): Flow<List<Transaction>> =
        dao.getAllTransactions().map { list -> list.map { it.toDomain() } }

    override suspend fun getTransactionById(id: Long): Transaction? =
        dao.getTransactionById(id)?.toDomain()

    override fun getTransactionsByDateRange(startDate: Long, endDate: Long): Flow<List<Transaction>> =
        dao.getTransactionsByDateRange(startDate, endDate).map { list -> list.map { it.toDomain() } }

    override fun getTransactionsByCategory(category: String): Flow<List<Transaction>> =
        dao.getTransactionsByCategory(category).map { list -> list.map { it.toDomain() } }

    override fun searchTransactions(query: String): Flow<List<Transaction>> =
        dao.searchTransactions(query).map { list -> list.map { it.toDomain() } }

    override fun getTotalIncome(startDate: Long, endDate: Long): Flow<Double> =
        dao.getTotalIncome(startDate, endDate).map { it ?: 0.0 }

    override fun getTotalExpense(startDate: Long, endDate: Long): Flow<Double> =
        dao.getTotalExpense(startDate, endDate).map { it ?: 0.0 }

    override suspend fun insertTransaction(transaction: Transaction): Long =
        dao.insertTransaction(transaction.toEntity())

    override suspend fun updateTransaction(transaction: Transaction) =
        dao.updateTransaction(transaction.toEntity())

    override suspend fun deleteTransaction(id: Long) =
        dao.deleteTransactionById(id)
}

