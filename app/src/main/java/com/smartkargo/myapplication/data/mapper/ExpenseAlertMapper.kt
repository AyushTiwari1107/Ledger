package com.smartkargo.myapplication.data.mapper

import com.smartkargo.myapplication.data.local.entity.ExpenseAlertEntity
import com.smartkargo.myapplication.domain.model.ExpenseAlert
import com.smartkargo.myapplication.domain.model.RepeatType

fun ExpenseAlertEntity.toDomain(): ExpenseAlert = ExpenseAlert(
    id = id,
    title = title,
    amount = amount,
    dueDate = dueDate,
    repeatType = try { RepeatType.valueOf(repeatType) } catch (_: Exception) { RepeatType.NONE },
    isEnabled = isEnabled,
    createdAt = createdAt
)

fun ExpenseAlert.toEntity(): ExpenseAlertEntity = ExpenseAlertEntity(
    id = id,
    title = title,
    amount = amount,
    dueDate = dueDate,
    repeatType = repeatType.name,
    isEnabled = isEnabled,
    createdAt = createdAt
)

