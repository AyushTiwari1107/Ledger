package com.smartkargo.myapplication.domain.model

data class Transaction(
    val id: Long = 0,
    val amount: Double,
    val category: String,
    val note: String,
    val type: TransactionType,
    val date: Long,
    val createdAt: Long = System.currentTimeMillis()
)

enum class TransactionType {
    INCOME, EXPENSE
}

enum class Category(val displayName: String) {
    SALARY("Salary"),
    FREELANCE("Freelance"),
    INVESTMENT("Investment"),
    FOOD("Food"),
    TRANSPORT("Transport"),
    SHOPPING("Shopping"),
    ENTERTAINMENT("Entertainment"),
    BILLS("Bills"),
    HEALTH("Health"),
    EDUCATION("Education"),
    OTHER("Other")
}
