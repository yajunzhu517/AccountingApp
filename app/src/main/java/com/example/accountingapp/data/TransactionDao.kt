package com.example.accountingapp.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.accountingapp.data.entities.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
interface TransactionDao {
    @Query("SELECT * FROM transactions ORDER BY date DESC, id DESC")
    fun getAll(): Flow<List<Transaction>>

    @Query("SELECT * FROM transactions WHERE type = :type ORDER BY date DESC, id DESC")
    fun getByType(type: String): Flow<List<Transaction>>

    @Query("SELECT * FROM transactions WHERE date >= :start AND date < :end ORDER BY date DESC, id DESC")
    fun getByDateRange(start: Long, end: Long): Flow<List<Transaction>>

    @Query("SELECT * FROM transactions WHERE id = :id LIMIT 1")
    suspend fun getById(id: Long): Transaction?

    @Query("SELECT * FROM transactions WHERE note LIKE '%' || :query || '%' ORDER BY date DESC")
    fun search(query: String): Flow<List<Transaction>>

    @Insert
    suspend fun insert(transaction: Transaction): Long

    @Update
    suspend fun update(transaction: Transaction)

    @Delete
    suspend fun delete(transaction: Transaction)

    @Query("SELECT SUM(amount) FROM transactions WHERE type = 'income' AND date >= :start AND date < :end")
    suspend fun getIncomeSum(start: Long, end: Long): Double?

    @Query("SELECT SUM(amount) FROM transactions WHERE type = 'expense' AND date >= :start AND date < :end")
    suspend fun getExpenseSum(start: Long, end: Long): Double?

    @Query("SELECT SUM(amount) FROM transactions WHERE type = :type AND categoryId = :categoryId AND date >= :start AND date < :end")
    suspend fun getSumByCategory(type: String, categoryId: Long, start: Long, end: Long): Double?

    @Query("SELECT SUM(amount) FROM transactions WHERE type = :type AND strftime('%Y-%m', date/1000, 'unixepoch') = :month")
    suspend fun getMonthlySum(type: String, month: String): Double?
}
