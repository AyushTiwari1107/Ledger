package com.smartkargo.myapplication.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.smartkargo.myapplication.data.local.dao.ExpenseAlertDao
import com.smartkargo.myapplication.data.local.dao.TransactionDao
import com.smartkargo.myapplication.data.local.entity.ExpenseAlertEntity
import com.smartkargo.myapplication.data.local.entity.TransactionEntity

@Database(
    entities = [TransactionEntity::class, ExpenseAlertEntity::class],
    version = 2,
    exportSchema = false
)
abstract class SmartExpenseDatabase : RoomDatabase() {
    abstract fun transactionDao(): TransactionDao
    abstract fun expenseAlertDao(): ExpenseAlertDao
}
