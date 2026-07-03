package com.example.accountingapp.ui.screens.statistics

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.accountingapp.ui.screens.transactions.formatMoney
import com.example.accountingapp.ui.theme.expenseColor
import com.example.accountingapp.ui.theme.incomeColor
import com.example.accountingapp.ui.viewmodel.StatisticsViewModel
import kotlin.math.min

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatisticsScreen(
    navController: NavController,
    viewModel: StatisticsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("日", "月", "年")

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("统计") })
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            TabRow(selectedTabIndex = selectedTab) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = { Text(title) }
                    )
                }
            }

            // Summary Cards
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                SummaryCard(title = "收入", amount = uiState.monthIncome, color = incomeColor)
                SummaryCard(title = "支出", amount = uiState.monthExpense, color = expenseColor)
            }

            // Simple Pie Chart (Ring)
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .aspectRatio(1.2f),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        "收支比例",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(top = 16.dp)
                    )
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        SimpleRingChart(
                            income = uiState.monthIncome,
                            expense = uiState.monthExpense
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SummaryCard(title: String, amount: Double, color: Color) {
    Card(
        modifier = Modifier
            .fillMaxWidth(0.45f)
            .height(80.dp),
        colors = CardDefaults.cardColors(
            containerColor = color.copy(alpha = 0.1f)
        )
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(title, style = MaterialTheme.typography.bodyMedium)
            Text(
                formatMoney(amount),
                color = color,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
        }
    }
}

@Composable
fun SimpleRingChart(income: Double, expense: Double) {
    val total = income + expense
    if (total <= 0) {
        Text("暂无数据", color = MaterialTheme.colorScheme.onSurfaceVariant)
        return
    }

    val incomeRatio = (income / total).toFloat()
    val expenseRatio = (expense / total).toFloat()

    Canvas(modifier = Modifier.fillMaxSize()) {
        val centerX = size.width / 2
        val centerY = size.height / 2
        val radius = min(size.width, size.height) / 2.5f
        val strokeWidth = radius * 0.3f

        // Draw expense arc (background/outer)
        drawArc(
            color = expenseColor.copy(alpha = 0.3f),
            startAngle = -90f,
            sweepAngle = 360f,
            useCenter = false,
            topLeft = Offset(centerX - radius, centerY - radius),
            size = Size(radius * 2, radius * 2),
            style = Stroke(width = strokeWidth)
        )

        // Draw income arc
        drawArc(
            color = incomeColor,
            startAngle = -90f,
            sweepAngle = 360f * incomeRatio,
            useCenter = false,
            topLeft = Offset(centerX - radius, centerY - radius),
            size = Size(radius * 2, radius * 2),
            style = Stroke(width = strokeWidth)
        )
    }

    Row(
        modifier = Modifier.padding(top = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .padding(end = 4.dp)
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    drawCircle(color = incomeColor)
                }
            }
            Text("收入 ${(incomeRatio * 100).toInt()}%", fontSize = 12.sp)
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .padding(end = 4.dp)
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    drawCircle(color = expenseColor)
                }
            }
            Text("支出 ${(expenseRatio * 100).toInt()}%", fontSize = 12.sp)
        }
    }
}