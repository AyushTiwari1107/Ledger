package com.smartkargo.myapplication.data.mapper

import com.smartkargo.myapplication.data.local.entity.TransactionEntity
import com.smartkargo.myapplication.domain.model.Transaction
import com.smartkargo.myapplication.domain.model.TransactionType

fun TransactionEntity.toDomain(): Transaction = Transaction(
    id = id,
    amount = amount,
    category = category,
    note = note,
    type = if (type.equals("income", ignoreCase = true)) TransactionType.INCOME else TransactionType.EXPENSE,
    date = date,
    createdAt = createdAt
)

fun Transaction.toEntity(): TransactionEntity = TransactionEntity(
    id = id,
    amount = amount,
    category = category,
    note = note,
    type = type.name.lowercase(),
    date = date,
    createdAt = createdAt
)
