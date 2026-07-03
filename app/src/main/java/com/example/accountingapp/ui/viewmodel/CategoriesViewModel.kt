package com.example.accountingapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.accountingapp.data.entities.Category
import com.example.accountingapp.data.repository.CategoryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CategoriesViewModel @Inject constructor(
    private val categoryRepository: CategoryRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CategoriesUiState())
    val uiState: StateFlow<CategoriesUiState> = _uiState.asStateFlow()

    init {
        loadCategories()
    }

    fun loadCategories() {
        categoryRepository.getAllCategories()
            .onEach { categories ->
                _uiState.value = _uiState.value.copy(categories = categories)
            }
            .launchIn(viewModelScope)
    }

    fun addCategory(name: String, type: String, icon: String, color: Int) {
        viewModelScope.launch {
            val category = Category(
                name = name,
                type = type,
                icon = icon,
                color = color,
                isPreset = false
            )
            categoryRepository.insertCategory(category)
        }
    }

    fun deleteCategory(category: Category) {
        viewModelScope.launch {
            categoryRepository.deleteCategory(category)
        }
    }
}

data class CategoriesUiState(
    val categories: List<Category> = emptyList()
)
