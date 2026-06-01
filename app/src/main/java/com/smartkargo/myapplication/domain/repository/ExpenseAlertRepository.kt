package com.smartkargo.myapplication.domain.repository
import com.smartkargo.myapplication.domain.model.ExpenseAlert
import kotlinx.coroutines.flow.Flow
interface ExpenseAlertRepository {
    fun getAllAlerts(): Flow<List<ExpenseAlert>>
    suspend fun getAlertById(id: Long): ExpenseAlert?
    fun getAlertsDueToday(): Flow<List<ExpenseAlert>>
    fun getUpcomingAlerts(daysAhead: Int = 7): Flow<List<ExpenseAlert>>
    suspend fun insertAlert(alert: ExpenseAlert): Long
    suspend fun updateAlert(alert: ExpenseAlert)
    suspend fun deleteAlert(id: Long)
}
