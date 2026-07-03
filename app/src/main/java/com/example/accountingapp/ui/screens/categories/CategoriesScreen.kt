package com.example.accountingapp.ui.screens.categories

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.accountingapp.data.entities.Category
import com.example.accountingapp.ui.screens.record.iconMap
import com.example.accountingapp.ui.viewmodel.CategoriesViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoriesScreen(
    navController: NavController,
    viewModel: CategoriesViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }
    var categoryToDelete by remember { mutableStateOf<Category?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("分类管理") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "返回")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddDialog = true }) {
                Icon(Icons.Filled.Add, contentDescription = "添加分类")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            Text(
                "支出分类",
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(16.dp)
            )
            LazyColumn(
                contentPadding = PaddingValues(horizontal = 16.dp)
            ) {
                items(uiState.categories.filter { it.type == "expense" }) { category ->
                    CategoryItem(
                        category = category,
                        onDelete = { categoryToDelete = category }
                    )
                }
            }

            Text(
                "收入分类",
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(16.dp)
            )
            LazyColumn(
                contentPadding = PaddingValues(horizontal = 16.dp)
            ) {
                items(uiState.categories.filter { it.type == "income" }) { category ->
                    CategoryItem(
                        category = category,
                        onDelete = { categoryToDelete = category }
                    )
                }
            }
        }
    }

    if (showAddDialog) {
        AddCategoryDialog(
            onDismiss = { showAddDialog = false },
            onConfirm = { name, type, icon, color ->
                viewModel.addCategory(name, type, icon, color)
                showAddDialog = false
            }
        )
    }

    if (categoryToDelete != null) {
        AlertDialog(
            onDismissRequest = { categoryToDelete = null },
            title = { Text("确认删除") },
            text = { Text("确定要删除分类 \"${categoryToDelete?.name}\" 吗？") },
            confirmButton = {
                TextButton(onClick = {
                    categoryToDelete?.let { viewModel.deleteCategory(it) }
                    categoryToDelete = null
                }) {
                    Text("删除", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { categoryToDelete = null }) {
                    Text("取消")
                }
            }
        )
    }
}

@Composable
fun CategoryItem(
    category: Category,
    onDelete: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable { },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(Color(category.color)),
            contentAlignment = Alignment.Center
        ) {
            val icon = iconMap[category.icon] ?: iconMap["MoreHoriz"]!!
            Icon(
                imageVector = icon,
                contentDescription = category.name,
                tint = Color.White,
                modifier = Modifier.size(20.dp)
            )
        }
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = category.name,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.weight(1f)
        )
        if (!category.isPreset) {
            IconButton(onClick = onDelete) {
                Icon(
                    Icons.Filled.Delete,
                    contentDescription = "删除",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddCategoryDialog(
    onDismiss: () -> Unit,
    onConfirm: (String, String, String, Int) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var type by remember { mutableStateOf("expense") }
    var selectedIcon by remember { mutableStateOf("MoreHoriz") }
    var selectedColor by remember { mutableStateOf(0xFF666666.toInt()) }

    val icons = listOf(
        "Restaurant", "DirectionsCar", "ShoppingCart", "Movie", "Home",
        "LocalHospital", "School", "Work", "CardGiftcard", "TrendingUp",
        "Construction", "Money", "MoreHoriz"
    )
    val colors = listOf(
        0xFFEF5350.toInt(), 0xFF42A5F5.toInt(), 0xFFAB47BC.toInt(),
        0xFFFF7043.toInt(), 0xFF66BB6A.toInt(), 0xFFEC407A.toInt(),
        0xFF29B6F6.toInt(), 0xFFFFCA28.toInt(), 0xFF26C6DA.toInt(),
        0xFF8D6E63.toInt(), 0xFF78909C.toInt(), 0xFF666666.toInt()
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("添加分类") },
        text = {
            Column {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("分类名称") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.padding(8.dp))
                Text("类型", style = MaterialTheme.typography.bodyMedium)
                Row {
                    TextButton(onClick = { type = "expense" }) {
                        Text("支出", color = if (type == "expense") MaterialTheme.colorScheme.primary else Color.Gray)
                    }
                    TextButton(onClick = { type = "income" }) {
                        Text("收入", color = if (type == "income") MaterialTheme.colorScheme.primary else Color.Gray)
                    }
                }
                Spacer(modifier = Modifier.padding(8.dp))
                Text("颜色", style = MaterialTheme.typography.bodyMedium)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    colors.take(6).forEach { color ->
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(Color(color))
                                .clickable { selectedColor = color }
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (name.isNotBlank()) {
                        onConfirm(name, type, selectedIcon, selectedColor)
                    }
                },
                enabled = name.isNotBlank()
            ) {
                Text("添加")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("取消")
            }
        }
    )
}