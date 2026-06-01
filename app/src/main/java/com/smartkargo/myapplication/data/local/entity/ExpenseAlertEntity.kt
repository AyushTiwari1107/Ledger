package com.smartkargo.myapplication.data.local.entity
import androidx.room.Entity
import androidx.room.PrimaryKey
@Entity(tableName = "expense_alerts")
data class ExpenseAlertEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val amount: Double,
    val dueDate: Long,
    val repeatType: String,
    val isEnabled: Boolean = true,
    val createdAt: Long = System.currentTimeMillis()
)
