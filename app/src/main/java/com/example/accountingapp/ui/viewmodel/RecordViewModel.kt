package com.example.accountingapp.ui.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.accountingapp.data.entities.Transaction
import com.example.accountingapp.data.repository.CategoryRepository
import com.example.accountingapp.data.repository.TransactionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

@HiltViewModel
class RecordViewModel @Inject constructor(
    private val transactionRepository: TransactionRepository,
    private val categoryRepository: CategoryRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(RecordUiState())
    val uiState: StateFlow<RecordUiState> = _uiState.asStateFlow()

    fun setType(type: String) {
        _uiState.value = _uiState.value.copy(type = type)
    }

    fun setAmount(amount: String) {
        _uiState.value = _uiState.value.copy(amount = amount)
    }

    fun setCategoryId(categoryId: Long) {
        _uiState.value = _uiState.value.copy(categoryId = categoryId)
    }

    fun setNote(note: String) {
        _uiState.value = _uiState.value.copy(note = note)
    }

    fun setDate(date: Long) {
        _uiState.value = _uiState.value.copy(date = date)
    }

    fun appendDigit(digit: String) {
        val current = _uiState.value.amount
        if (current == "0" && digit != ".") {
            _uiState.value = _uiState.value.copy(amount = digit)
        } else if (digit == "." && current.contains(".")) {
            // ignore
        } else {
            _uiState.value = _uiState.value.copy(amount = current + digit)
        }
    }

    fun backspace() {
        val current = _uiState.value.amount
        if (current.length > 1) {
            _uiState.value = _uiState.value.copy(amount = current.dropLast(1))
        } else {
            _uiState.value = _uiState.value.copy(amount = "0")
        }
    }

    fun saveTransaction(onSuccess: () -> Unit) {
        viewModelScope.launch {
            val amount = _uiState.value.amount.toDoubleOrNull() ?: 0.0
            if (amount <= 0) return@launch

            val transaction = Transaction(
                amount = amount,
                type = _uiState.value.type,
                categoryId = _uiState.value.categoryId,
                date = _uiState.value.date,
                note = _uiState.value.note
            )
            transactionRepository.insertTransaction(transaction)
            onSuccess()
        }
    }

    fun loadTransaction(id: Long) {
        viewModelScope.launch {
            val transaction = transactionRepository.getTransactionById(id)
            transaction?.let {
                _uiState.value = _uiState.value.copy(
                    amount = it.amount.toString(),
                    type = it.type,
                    categoryId = it.categoryId,
                    note = it.note,
                    date = it.date,
                    editingId = it.id
                )
            }
        }
    }

    fun updateTransaction(onSuccess: () -> Unit) {
        viewModelScope.launch {
            val id = _uiState.value.editingId ?: return@launch
            val amount = _uiState.value.amount.toDoubleOrNull() ?: 0.0
            if (amount <= 0) return@launch

            val transaction = Transaction(
                id = id,
                amount = amount,
                type = _uiState.value.type,
                categoryId = _uiState.value.categoryId,
                date = _uiState.value.date,
                note = _uiState.value.note
            )
            transactionRepository.updateTransaction(transaction)
            onSuccess()
        }
    }
}

data class RecordUiState(
    val amount: String = "0",
    val type: String = "expense",
    val categoryId: Long? = null,
    val note: String = "",
    val date: Long = System.currentTimeMillis(),
    val editingId: Long? = null
)
