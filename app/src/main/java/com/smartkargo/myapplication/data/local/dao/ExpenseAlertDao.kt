package com.smartkargo.myapplication.data.local.dao

import androidx.room.*
import com.smartkargo.myapplication.data.local.entity.ExpenseAlertEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ExpenseAlertDao {

    @Query("SELECT * FROM expense_alerts ORDER BY dueDate ASC")
    fun getAllAlerts(): Flow<List<ExpenseAlertEntity>>

    @Query("SELECT * FROM expense_alerts WHERE id = :id")
    suspend fun getAlertById(id: Long): ExpenseAlertEntity?

    @Query("SELECT * FROM expense_alerts WHERE isEnabled = 1 AND dueDate BETWEEN :startOfDay AND :endOfDay")
    fun getAlertsDueToday(startOfDay: Long, endOfDay: Long): Flow<List<ExpenseAlertEntity>>

    @Query("SELECT * FROM expense_alerts WHERE isEnabled = 1 AND dueDate BETWEEN :startDate AND :endDate")
    fun getUpcomingAlerts(startDate: Long, endDate: Long): Flow<List<ExpenseAlertEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAlert(alert: ExpenseAlertEntity): Long

    @Update
    suspend fun updateAlert(alert: ExpenseAlertEntity)

    @Query("DELETE FROM expense_alerts WHERE id = :id")
    suspend fun deleteAlert(id: Long)
}
