package com.example.accountingapp.data.repository

import com.example.accountingapp.data.CategoryDao
import com.example.accountingapp.data.entities.Category
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CategoryRepository @Inject constructor(
    private val categoryDao: CategoryDao
) {
    fun getAllCategories(): Flow<List<Category>> = categoryDao.getAll()

    fun getCategoriesByType(type: String): Flow<List<Category>> = categoryDao.getByType(type)

    suspend fun getCategoryById(id: Long): Category? = categoryDao.getById(id)

    suspend fun insertCategory(category: Category): Long = categoryDao.insert(category)

    suspend fun updateCategory(category: Category) = categoryDao.update(category)

    suspend fun deleteCategory(category: Category) = categoryDao.delete(category)

    suspend fun deleteAllCustomCategories() = categoryDao.deleteAllCustom()
}
