package com.smartkargo.myapplication.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.ListenableWorker
import androidx.work.WorkerParameters
import com.smartkargo.myapplication.R
import com.smartkargo.myapplication.domain.repository.ExpenseAlertRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.first
import java.util.Locale

@HiltWorker
class AlertNotificationWorker @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted workerParams: WorkerParameters,
    private val repository: ExpenseAlertRepository
) : CoroutineWorker(context, workerParams) {

    companion object {
        const val CHANNEL_ID = "expense_alerts"
        const val CHANNEL_NAME = "Ledger Alerts"
        const val WORK_NAME = "expense_alert_check"
    }

    override suspend fun doWork(): ListenableWorker.Result {
        createNotificationChannel()

        val todayAlerts = repository.getAlertsDueToday().first()
        val upcomingAlerts = repository.getUpcomingAlerts(3).first()

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Notify about today's payments
        todayAlerts.forEach { alert ->
            val notification = NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle("Payment Due Today!")
                .setContentText("${alert.title} - $${String.format(Locale.getDefault(), "%.2f", alert.amount)}")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .build()
            notificationManager.notify(alert.id.toInt(), notification)
        }

        // Notify about upcoming payments
        val upcomingOnly = upcomingAlerts.filter { u -> todayAlerts.none { t -> t.id == u.id } }
        if (upcomingOnly.isNotEmpty()) {
            val text = upcomingOnly.joinToString(", ") { a ->
                "${a.title} ($${String.format(Locale.getDefault(), "%.2f", a.amount)})"
            }
            val notification = NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle("Upcoming Expenses")
                .setContentText(text)
                .setStyle(NotificationCompat.BigTextStyle().bigText(text))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true)
                .build()
            notificationManager.notify(9999, notification)
        }

        return ListenableWorker.Result.success()
    }

    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            CHANNEL_ID,
            CHANNEL_NAME,
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "Notifications for expense alerts and payment reminders"
        }
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.createNotificationChannel(channel)
    }
}
