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
import javax.inject.Inject

@HiltViewModel
class TransactionsViewModel @Inject constructor(
    private val transactionRepository: TransactionRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(TransactionsUiState())
    val uiState: StateFlow<TransactionsUiState> = _uiState.asStateFlow()

    init {
        loadAll()
    }

    fun loadAll() {
        transactionRepository.getAllTransactions()
            .onEach { transactions ->
                _uiState.value = _uiState.value.copy(transactions = transactions)
            }
            .launchIn(viewModelScope)
    }

    fun filterByType(type: String?) {
        if (type == null) {
            loadAll()
            return
        }
        transactionRepository.getTransactionsByType(type)
            .onEach { transactions ->
                _uiState.value = _uiState.value.copy(transactions = transactions)
            }
            .launchIn(viewModelScope)
    }

    fun search(query: String) {
        if (query.isBlank()) {
            loadAll()
            return
        }
        transactionRepository.searchTransactions(query)
            .onEach { transactions ->
                _uiState.value = _uiState.value.copy(transactions = transactions)
            }
            .launchIn(viewModelScope)
    }

    fun deleteTransaction(transaction: Transaction) {
        viewModelScope.launch {
            transactionRepository.deleteTransaction(transaction)
        }
    }
}

data class TransactionsUiState(
    val transactions: List<Transaction> = emptyList()
)
