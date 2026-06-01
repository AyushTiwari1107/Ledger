package com.smartkargo.myapplication.domain.model
data class ExpenseAlert(
    val id: Long = 0,
    val title: String,
    val amount: Double,
    val dueDate: Long,
    val repeatType: RepeatType = RepeatType.NONE,
    val isEnabled: Boolean = true,
    val createdAt: Long = System.currentTimeMillis()
)
enum class RepeatType(val displayName: String) {
    NONE("One-time"),
    DAILY("Daily"),
    WEEKLY("Weekly"),
    MONTHLY("Monthly"),
    YEARLY("Yearly")
}
