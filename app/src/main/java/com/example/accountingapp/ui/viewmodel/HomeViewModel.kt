package com.example.accountingapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.accountingapp.data.entities.Transaction
import com.example.accountingapp.data.repository.TransactionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val transactionRepository: TransactionRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.DAY_OF_MONTH, 1)
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        val startOfMonth = calendar.timeInMillis

        calendar.add(Calendar.MONTH, 1)
        val endOfMonth = calendar.timeInMillis

        viewModelScope.launch {
            val income = transactionRepository.getIncomeSum(startOfMonth, endOfMonth)
            val expense = transactionRepository.getExpenseSum(startOfMonth, endOfMonth)
            _uiState.value = _uiState.value.copy(
                monthIncome = income,
                monthExpense = expense
            )
        }

        transactionRepository.getAllTransactions()
            .onEach { transactions ->
                _uiState.value = _uiState.value.copy(
                    recentTransactions = transactions.take(10)
                )
            }
            .launchIn(viewModelScope)
    }
}

data class HomeUiState(
    val monthIncome: Double = 0.0,
    val monthExpense: Double = 0.0,
    val recentTransactions: List<Transaction> = emptyList()
)
