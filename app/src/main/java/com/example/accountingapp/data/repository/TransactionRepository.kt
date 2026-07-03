package com.example.accountingapp.data.repository

import com.example.accountingapp.data.TransactionDao
import com.example.accountingapp.data.entities.Transaction
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TransactionRepository @Inject constructor(
    private val transactionDao: TransactionDao
) {
    fun getAllTransactions(): Flow<List<Transaction>> = transactionDao.getAll()

    fun getTransactionsByType(type: String): Flow<List<Transaction>> = transactionDao.getByType(type)

    fun getTransactionsByDateRange(start: Long, end: Long): Flow<List<Transaction>> =
        transactionDao.getByDateRange(start, end)

    suspend fun getTransactionById(id: Long): Transaction? = transactionDao.getById(id)

    fun searchTransactions(query: String): Flow<List<Transaction>> = transactionDao.search(query)

    suspend fun insertTransaction(transaction: Transaction): Long = transactionDao.insert(transaction)

    suspend fun updateTransaction(transaction: Transaction) = transactionDao.update(transaction)

    suspend fun deleteTransaction(transaction: Transaction) = transactionDao.delete(transaction)

    suspend fun getIncomeSum(start: Long, end: Long): Double =
        transactionDao.getIncomeSum(start, end) ?: 0.0

    suspend fun getExpenseSum(start: Long, end: Long): Double =
        transactionDao.getExpenseSum(start, end) ?: 0.0

    suspend fun getSumByCategory(type: String, categoryId: Long, start: Long, end: Long): Double =
        transactionDao.getSumByCategory(type, categoryId, start, end) ?: 0.0

    suspend fun getMonthlySum(type: String, month: String): Double =
        transactionDao.getMonthlySum(type, month) ?: 0.0
}
