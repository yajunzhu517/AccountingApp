package com.example.accountingapp.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.accountingapp.data.entities.Account
import com.example.accountingapp.data.entities.Category
import com.example.accountingapp.data.entities.Transaction

@Database(
    entities = [Transaction::class, Category::class, Account::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun transactionDao(): TransactionDao
    abstract fun categoryDao(): CategoryDao
    abstract fun accountDao(): AccountDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "accounting.db"
                ).build()
                INSTANCE = instance
                instance
            }
        }

        suspend fun populateInitialData(context: Context) {
            val database = getDatabase(context)
            val categoryDao = database.categoryDao()
            if (categoryDao.count() == 0) {
                val presetCategories = listOf(
                    // Expense categories
                    Category(id = 0, name = "餐饮", type = "expense", icon = "Restaurant", color = 0xFFEF5350.toInt(), sortOrder = 0, isPreset = true),
                    Category(id = 0, name = "交通", type = "expense", icon = "DirectionsCar", color = 0xFF42A5F5.toInt(), sortOrder = 1, isPreset = true),
                    Category(id = 0, name = "购物", type = "expense", icon = "ShoppingCart", color = 0xFFAB47BC.toInt(), sortOrder = 2, isPreset = true),
                    Category(id = 0, name = "娱乐", type = "expense", icon = "Movie", color = 0xFFFF7043.toInt(), sortOrder = 3, isPreset = true),
                    Category(id = 0, name = "居住", type = "expense", icon = "Home", color = 0xFF66BB6A.toInt(), sortOrder = 4, isPreset = true),
                    Category(id = 0, name = "医疗", type = "expense", icon = "LocalHospital", color = 0xFFEC407A.toInt(), sortOrder = 5, isPreset = true),
                    Category(id = 0, name = "教育", type = "expense", icon = "School", color = 0xFF29B6F6.toInt(), sortOrder = 6, isPreset = true),
                    Category(id = 0, name = "其他支出", type = "expense", icon = "MoreHoriz", color = 0xFF8D6E63.toInt(), sortOrder = 7, isPreset = true),
                    // Income categories
                    Category(id = 0, name = "工资", type = "income", icon = "Work", color = 0xFF66BB6A.toInt(), sortOrder = 0, isPreset = true),
                    Category(id = 0, name = "奖金", type = "income", icon = "CardGiftcard", color = 0xFFFFCA28.toInt(), sortOrder = 1, isPreset = true),
                    Category(id = 0, name = "理财", type = "income", icon = "TrendingUp", color = 0xFF26C6DA.toInt(), sortOrder = 2, isPreset = true),
                    Category(id = 0, name = "兼职", type = "income", icon = "Construction", color = 0xFF8D6E63.toInt(), sortOrder = 3, isPreset = true),
                    Category(id = 0, name = "其他收入", type = "income", icon = "MoreHoriz", color = 0xFF78909C.toInt(), sortOrder = 4, isPreset = true)
                )
                presetCategories.forEach { categoryDao.insert(it) }
            }
        }
    }
}
