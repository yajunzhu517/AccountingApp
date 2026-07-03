package com.example.accountingapp.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "categories")
data class Category(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val type: String, // "income" or "expense"
    val icon: String, // material icon name
    val color: Int,   // ARGB color int
    val sortOrder: Int = 0,
    val isPreset: Boolean = false
)
