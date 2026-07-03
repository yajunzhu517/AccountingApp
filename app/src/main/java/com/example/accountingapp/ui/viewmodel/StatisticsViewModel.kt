package com.example.accountingapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.accountingapp.data.repository.TransactionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

@HiltViewModel
class StatisticsViewModel @Inject constructor(
    private val transactionRepository: TransactionRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(StatisticsUiState())
    val uiState: StateFlow<StatisticsUiState> = _uiState.asStateFlow()

    init {
        loadMonthlyData()
    }

    fun loadMonthlyData() {
        viewModelScope.launch {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH) + 1
            val monthStr = String.format("%04d-%02d", year, month)

            val income = transactionRepository.getMonthlySum("income", monthStr)
            val expense = transactionRepository.getMonthlySum("expense", monthStr)

            _uiState.value = _uiState.value.copy(
                monthIncome = income,
                monthExpense = expense
            )
        }
    }
}

data class StatisticsUiState(
    val monthIncome: Double = 0.0,
    val monthExpense: Double = 0.0,
    val period: String = "month"
)
