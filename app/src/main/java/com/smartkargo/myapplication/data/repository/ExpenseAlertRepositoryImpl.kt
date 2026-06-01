package com.smartkargo.myapplication.data.repository

import com.smartkargo.myapplication.data.local.dao.ExpenseAlertDao
import com.smartkargo.myapplication.data.mapper.toDomain
import com.smartkargo.myapplication.data.mapper.toEntity
import com.smartkargo.myapplication.domain.model.ExpenseAlert
import com.smartkargo.myapplication.domain.repository.ExpenseAlertRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.*
import javax.inject.Inject

class ExpenseAlertRepositoryImpl @Inject constructor(
    private val dao: ExpenseAlertDao
) : ExpenseAlertRepository {

    override fun getAllAlerts(): Flow<List<ExpenseAlert>> =
        dao.getAllAlerts().map { list -> list.map { it.toDomain() } }

    override suspend fun getAlertById(id: Long): ExpenseAlert? =
        dao.getAlertById(id)?.toDomain()

    override fun getAlertsDueToday(): Flow<List<ExpenseAlert>> {
        val cal = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val startOfDay = cal.timeInMillis
        cal.add(Calendar.DAY_OF_YEAR, 1)
        val endOfDay = cal.timeInMillis
        return dao.getAlertsDueToday(startOfDay, endOfDay).map { list -> list.map { it.toDomain() } }
    }

    override fun getUpcomingAlerts(daysAhead: Int): Flow<List<ExpenseAlert>> {
        val now = System.currentTimeMillis()
        val cal = Calendar.getInstance().apply {
            add(Calendar.DAY_OF_YEAR, daysAhead)
        }
        val endDate = cal.timeInMillis
        return dao.getUpcomingAlerts(now, endDate).map { list -> list.map { it.toDomain() } }
    }

    override suspend fun insertAlert(alert: ExpenseAlert): Long =
        dao.insertAlert(alert.toEntity())

    override suspend fun updateAlert(alert: ExpenseAlert) =
        dao.updateAlert(alert.toEntity())

    override suspend fun deleteAlert(id: Long) =
        dao.deleteAlert(id)
}

