package com.smartkargo.myapplication.presentation.screen.analytics

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.smartkargo.myapplication.domain.model.CategoryBreakdown
import com.smartkargo.myapplication.domain.model.DashboardSummary
import java.util.Locale

val chartColors = listOf(
    Color(0xFF4CAF50), Color(0xFFF44336), Color(0xFF2196F3),
    Color(0xFFFF9800), Color(0xFF9C27B0), Color(0xFF009688),
    Color(0xFFFFEB3B), Color(0xFF795548), Color(0xFFE91E63),
    Color(0xFF607D8B), Color(0xFF3F51B5)
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnalyticsScreen(
    onBack: () -> Unit,
    viewModel: AnalyticsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Analytics & Charts") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                }
            )
        }
    ) { padding ->
        if (uiState.isLoading) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            Column(
                Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 16.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                Spacer(Modifier.height(4.dp))

                // Summary Cards
                Text("This Week", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                ReportCard(uiState.weeklySummary)

                Text("This Month", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                ReportCard(uiState.monthlySummary)

                // Monthly Bar Chart
                if (uiState.monthlyData.isNotEmpty()) {
                    Text(
                        "Monthly Spending (Last 6 Months)",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    MonthlyBarChart(data = uiState.monthlyData)

                    // Which month spent most / least
                    val maxMonth = uiState.monthlyData.maxByOrNull { it.expense }
                    val minMonth = uiState.monthlyData.filter { it.expense > 0 }.minByOrNull { it.expense }
                    if (maxMonth != null) {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                        ) {
                            Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text("📈 Most spent: ${maxMonth.label} ($${String.format("%.2f", maxMonth.expense)})", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                                if (minMonth != null) {
                                    Text("📉 Least spent: ${minMonth.label} ($${String.format("%.2f", minMonth.expense)})", style = MaterialTheme.typography.bodyMedium)
                                }
                            }
                        }
                    }
                }

                // Income vs Expense Line Chart
                if (uiState.monthlyData.isNotEmpty()) {
                    Text(
                        "Income vs Expense Trend",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    IncomeExpenseLineChart(data = uiState.monthlyData)
                    // Legend
                    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        LegendItem(color = Color(0xFF4CAF50), label = "Income")
                        LegendItem(color = Color(0xFFF44336), label = "Expense")
                    }
                }

                // Category Donut Chart
                if (uiState.categoryBreakdown.isNotEmpty()) {
                    Text(
                        "Spending by Category",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Row(
                        Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        DonutChart(
                            items = uiState.categoryBreakdown,
                            modifier = Modifier.size(180.dp)
                        )
                        Spacer(Modifier.width(16.dp))
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            uiState.categoryBreakdown.forEachIndexed { i, item ->
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Box(
                                        Modifier
                                            .size(12.dp)
                                            .background(chartColors[i % chartColors.size], CircleShape)
                                    )
                                    Text(
                                        "${item.category}: ${"%.0f".format(item.percentage)}%",
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                }
                            }
                        }
                    }

                    // Category progress bars
                    Text("Category Details", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                    uiState.categoryBreakdown.forEachIndexed { index, item ->
                        Column {
                            Row(
                                Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(item.category, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Medium)
                                Text("$${String.format("%.2f", item.amount)} (${"%.0f".format(item.percentage)}%)", style = MaterialTheme.typography.bodySmall)
                            }
                            Spacer(Modifier.height(4.dp))
                            LinearProgressIndicator(
                                progress = { item.percentage / 100f },
                                modifier = Modifier.fillMaxWidth().height(10.dp),
                                color = chartColors[index % chartColors.size],
                                trackColor = MaterialTheme.colorScheme.surfaceVariant
                            )
                        }
                    }
                }

                Spacer(Modifier.height(24.dp))
            }
        }
    }
}

@Composable
fun MonthlyBarChart(data: List<MonthlyData>) {
    val maxVal = data.maxOf { it.expense }.takeIf { it > 0 } ?: 1.0
    val barColor = Color(0xFFF44336)
    val labelColor = MaterialTheme.colorScheme.onSurface

    Card(Modifier.fillMaxWidth()) {
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            val barWidth = size.width / (data.size * 2f)
            val chartHeight = size.height - 30.dp.toPx()

            data.forEachIndexed { i, item ->
                val barHeight = ((item.expense / maxVal) * chartHeight).toFloat()
                val x = i * (size.width / data.size) + barWidth / 2

                // Draw bar
                drawRect(
                    color = barColor,
                    topLeft = Offset(x, chartHeight - barHeight),
                    size = Size(barWidth, barHeight)
                )

                // Draw label
                drawContext.canvas.nativeCanvas.drawText(
                    item.label,
                    x + barWidth / 2,
                    size.height,
                    android.graphics.Paint().apply {
                        color = labelColor.toArgb()
                        textSize = 28f
                        textAlign = android.graphics.Paint.Align.CENTER
                    }
                )

                // Draw amount on top
                if (item.expense > 0) {
                    drawContext.canvas.nativeCanvas.drawText(
                        "$${item.expense.toInt()}",
                        x + barWidth / 2,
                        chartHeight - barHeight - 4.dp.toPx(),
                        android.graphics.Paint().apply {
                            color = labelColor.toArgb()
                            textSize = 22f
                            textAlign = android.graphics.Paint.Align.CENTER
                        }
                    )
                }
            }

            // Baseline
            drawLine(
                color = labelColor.copy(alpha = 0.3f),
                start = Offset(0f, chartHeight),
                end = Offset(size.width, chartHeight),
                strokeWidth = 2f
            )
        }
    }
}

@Composable
fun IncomeExpenseLineChart(data: List<MonthlyData>) {
    val maxVal = data.maxOf { maxOf(it.income, it.expense) }.takeIf { it > 0 } ?: 1.0
    val incomeColor = Color(0xFF4CAF50)
    val expenseColor = Color(0xFFF44336)
    val gridColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.15f)

    Card(Modifier.fillMaxWidth()) {
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .padding(horizontal = 16.dp, vertical = 16.dp)
        ) {
            val chartHeight = size.height - 20.dp.toPx()
            val stepX = size.width / (data.size - 1).coerceAtLeast(1)

            // Horizontal grid lines
            repeat(4) { i ->
                val y = chartHeight - (i / 4f) * chartHeight
                drawLine(color = gridColor, start = Offset(0f, y), end = Offset(size.width, y), strokeWidth = 1f)
            }

            fun pointX(i: Int) = i * stepX
            fun pointY(value: Double) = (chartHeight - (value / maxVal * chartHeight)).toFloat()

            // Draw income line
            for (i in 0 until data.size - 1) {
                drawLine(
                    color = incomeColor,
                    start = Offset(pointX(i), pointY(data[i].income)),
                    end = Offset(pointX(i + 1), pointY(data[i + 1].income)),
                    strokeWidth = 4f
                )
            }

            // Draw expense line
            for (i in 0 until data.size - 1) {
                drawLine(
                    color = expenseColor,
                    start = Offset(pointX(i), pointY(data[i].expense)),
                    end = Offset(pointX(i + 1), pointY(data[i + 1].expense)),
                    strokeWidth = 4f
                )
            }

            // Draw dots
            data.forEachIndexed { i, item ->
                drawCircle(color = incomeColor, radius = 6f, center = Offset(pointX(i), pointY(item.income)))
                drawCircle(color = expenseColor, radius = 6f, center = Offset(pointX(i), pointY(item.expense)))
            }
        }
    }
}

@Composable
fun DonutChart(items: List<CategoryBreakdown>, modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val strokeWidth = size.width * 0.22f
        val radius = (size.width - strokeWidth) / 2
        var startAngle = -90f

        items.forEachIndexed { i, item ->
            val sweep = item.percentage / 100f * 360f
            drawArc(
                color = chartColors[i % chartColors.size],
                startAngle = startAngle,
                sweepAngle = sweep,
                useCenter = false,
                topLeft = Offset(strokeWidth / 2, strokeWidth / 2),
                size = Size(radius * 2, radius * 2),
                style = androidx.compose.ui.graphics.drawscope.Stroke(width = strokeWidth)
            )
            startAngle += sweep
        }
    }
}

@Composable
fun LegendItem(color: Color, label: String) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
        Box(Modifier.size(12.dp).background(color, CircleShape))
        Text(label, style = MaterialTheme.typography.bodySmall)
    }
}

@Composable
fun ReportCard(summary: DashboardSummary) {
    Card(Modifier.fillMaxWidth()) {
        Row(
            Modifier.fillMaxWidth().padding(16.dp),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Income", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text("$${String.format(Locale.getDefault(), "%.2f", summary.totalIncome)}", style = MaterialTheme.typography.titleSmall, color = Color(0xFF4CAF50), fontWeight = FontWeight.Bold)
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Expense", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text("$${String.format(Locale.getDefault(), "%.2f", summary.totalExpense)}", style = MaterialTheme.typography.titleSmall, color = Color(0xFFF44336), fontWeight = FontWeight.Bold)
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Savings", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text("$${String.format(Locale.getDefault(), "%.2f", summary.savings)}", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
            }
        }
    }
}
