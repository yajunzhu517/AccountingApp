package com.example.accountingapp.ui.screens.record

import android.app.DatePickerDialog
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.Work
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.Construction
import androidx.compose.material.icons.filled.Money
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.accountingapp.ui.theme.ExpenseColor
import com.example.accountingapp.ui.theme.ExpenseColorLight
import com.example.accountingapp.ui.theme.IncomeColor
import com.example.accountingapp.ui.theme.IncomeColorLight
import com.example.accountingapp.ui.viewmodel.RecordViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

val iconMap = mapOf(
    "Restaurant" to Icons.Filled.Restaurant,
    "DirectionsCar" to Icons.Filled.DirectionsCar,
    "ShoppingCart" to Icons.Filled.ShoppingCart,
    "Movie" to Icons.Filled.Movie,
    "Home" to Icons.Filled.Home,
    "LocalHospital" to Icons.Filled.LocalHospital,
    "School" to Icons.Filled.School,
    "MoreHoriz" to Icons.Filled.MoreHoriz,
    "Work" to Icons.Filled.Work,
    "CardGiftcard" to Icons.Filled.CardGiftcard,
    "TrendingUp" to Icons.Filled.TrendingUp,
    "Construction" to Icons.Filled.Construction,
    "Money" to Icons.Filled.Money,
    "AccountBalance" to Icons.Filled.AccountBalance,
    "Savings" to Icons.Filled.Savings,
    "Payment" to Icons.Filled.Payment,
    "CreditCard" to Icons.Filled.CreditCard,
    "AttachMoney" to Icons.Filled.AttachMoney
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecordScreen(
    navController: NavController,
    transactionId: Long? = null,
    viewModel: RecordViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    val dateFormatter = remember { SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()) }

    if (transactionId != null && uiState.editingId == null) {
        viewModel.loadTransaction(transactionId)
    }

    val datePickerDialog = remember(uiState.date) {
        val cal = Calendar.getInstance().apply { timeInMillis = uiState.date }
        DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                val newCal = Calendar.getInstance().apply {
                    set(year, month, dayOfMonth, 0, 0, 0)
                    set(Calendar.MILLISECOND, 0)
                }
                viewModel.setDate(newCal.timeInMillis)
            },
            cal.get(Calendar.YEAR),
            cal.get(Calendar.MONTH),
            cal.get(Calendar.DAY_OF_MONTH)
        )
    }

    val primaryColor = if (uiState.type == "income") IncomeColor else ExpenseColor
    val primaryColorLight = if (uiState.type == "income") IncomeColorLight else ExpenseColorLight

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        if (uiState.editingId != null) "编辑账单" else "记一笔",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "返回")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 20.dp)
                .background(MaterialTheme.colorScheme.background)
        ) {
            // Type Selector with modern chips
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                FilterChip(
                    selected = uiState.type == "expense",
                    onClick = { viewModel.setType("expense") },
                    label = { Text("支出", fontWeight = FontWeight.SemiBold) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = ExpenseColorLight,
                        selectedLabelColor = ExpenseColor
                    ),
                    shape = RoundedCornerShape(20.dp)
                )
                Spacer(modifier = Modifier.size(16.dp))
                FilterChip(
                    selected = uiState.type == "income",
                    onClick = { viewModel.setType("income") },
                    label = { Text("收入", fontWeight = FontWeight.SemiBold) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = IncomeColorLight,
                        selectedLabelColor = IncomeColor
                    ),
                    shape = RoundedCornerShape(20.dp)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Amount Display with modern card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = primaryColorLight
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.End
                ) {
                    Text(
                        text = "金额",
                        style = MaterialTheme.typography.bodySmall,
                        color = primaryColor.copy(alpha = 0.7f)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "¥${uiState.amount}",
                        fontSize = 42.sp,
                        fontWeight = FontWeight.Bold,
                        color = primaryColor
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Date Selector
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { datePickerDialog.show() },
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Filled.CalendarToday,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = dateFormatter.format(Date(uiState.date)),
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Note
            OutlinedTextField(
                value = uiState.note,
                onValueChange = { viewModel.setNote(it) },
                label = { Text("备注 (可选)") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = primaryColor,
                    focusedLabelColor = primaryColor
                ),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Number Pad
            ModernNumberPad(
                viewModel = viewModel,
                primaryColor = primaryColor
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Save Button
            Button(
                onClick = {
                    if (uiState.editingId != null) {
                        viewModel.updateTransaction {
                            navController.popBackStack()
                        }
                    } else {
                        viewModel.saveTransaction {
                            navController.popBackStack()
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = primaryColor
                ),
                enabled = uiState.amount.toDoubleOrNull()?.let { it > 0 } ?: false
            ) {
                Icon(Icons.Filled.Check, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("保存", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

@Composable
fun ModernNumberPad(
    viewModel: RecordViewModel,
    primaryColor: Color
) {
    val buttons = listOf(
        listOf("7", "8", "9", "DEL"),
        listOf("4", "5", "6", "+"),
        listOf("1", "2", "3", "-"),
        listOf("0", ".", "C", "OK")
    )

    Column(modifier = Modifier.fillMaxWidth()) {
        buttons.forEach { row ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                row.forEach { label ->
                    val isAction = label in listOf("DEL", "C", "OK", "+", "-")
                    val bgColor = when (label) {
                        "DEL", "C" -> MaterialTheme.colorScheme.errorContainer
                        "OK" -> primaryColor
                        "+", "-" -> primaryColor.copy(alpha = 0.15f)
                        else -> MaterialTheme.colorScheme.surfaceVariant
                    }
                    val textColor = when (label) {
                        "DEL", "C" -> MaterialTheme.colorScheme.onErrorContainer
                        "OK" -> Color.White
                        "+", "-" -> primaryColor
                        else -> MaterialTheme.colorScheme.onSurfaceVariant
                    }

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .aspectRatio(1.3f)
                            .clip(RoundedCornerShape(14.dp))
                            .background(bgColor)
                            .clickable {
                                when (label) {
                                    "DEL" -> viewModel.backspace()
                                    "C" -> viewModel.setAmount("0")
                                    "OK" -> {}
                                    else -> viewModel.appendDigit(label)
                                }
                            }
                            .padding(8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = label,
                            fontSize = if (label.length > 1) 14.sp else 22.sp,
                            fontWeight = if (isAction) FontWeight.SemiBold else FontWeight.Medium,
                            color = textColor
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}
