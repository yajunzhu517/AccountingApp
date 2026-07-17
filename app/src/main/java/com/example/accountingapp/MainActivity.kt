package com.example.accountingapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.BarChart
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.List
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.accountingapp.data.AppDatabase
import com.example.accountingapp.ui.screens.categories.CategoriesScreen
import com.example.accountingapp.ui.screens.home.HomeScreen
import com.example.accountingapp.ui.screens.record.RecordScreen
import com.example.accountingapp.ui.screens.settings.SettingsScreen
import com.example.accountingapp.ui.screens.statistics.StatisticsScreen
import com.example.accountingapp.ui.screens.transactions.TransactionsScreen
import com.example.accountingapp.ui.theme.AccountingAppTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lifecycleScope.launch {
            AppDatabase.populateInitialData(applicationContext)
        }
        setContent {
            AccountingAppTheme {
                AppNavigation()
            }
        }
    }
}

sealed class BottomNavItem(
    val route: String,
    val title: String,
    val icon: ImageVector,
    val selectedIcon: ImageVector
) {
    object Home : BottomNavItem("home", "首页", Icons.Outlined.Home, Icons.Filled.Home)
    object Transactions : BottomNavItem("transactions", "账单", Icons.Outlined.List, Icons.Filled.List)
    object Statistics : BottomNavItem("statistics", "统计", Icons.Outlined.BarChart, Icons.Filled.BarChart)
    object Settings : BottomNavItem("settings", "设置", Icons.Outlined.Settings, Icons.Filled.Settings)
}

val bottomNavItems = listOf(
    BottomNavItem.Home,
    BottomNavItem.Transactions,
    BottomNavItem.Statistics,
    BottomNavItem.Settings
)

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // Check if current route is a main tab (show bottom nav) or detail page (hide bottom nav)
    val isMainScreen = bottomNavItems.any { it.route == currentRoute }

    Scaffold(
        bottomBar = {
            if (isMainScreen) {
                NavigationBar(
                    modifier = Modifier
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                        .clip(RoundedCornerShape(24.dp)),
                    tonalElevation = 2.dp,
                    containerColor = MaterialTheme.colorScheme.surface
                ) {
                    bottomNavItems.forEach { item ->
                        val selected = currentRoute == item.route
                        NavigationBarItem(
                            icon = {
                                Icon(
                                    if (selected) item.selectedIcon else item.icon,
                                    contentDescription = item.title
                                )
                            },
                            label = { Text(item.title) },
                            selected = selected,
                            onClick = {
                                navController.navigate(item.route) {
                                    popUpTo(navController.graph.startDestinationId) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = MaterialTheme.colorScheme.primary,
                                selectedTextColor = MaterialTheme.colorScheme.primary,
                                indicatorColor = MaterialTheme.colorScheme.primaryContainer
                            )
                        )
                    }
                }
            }
        },
        floatingActionButton = {
            if (currentRoute == BottomNavItem.Home.route) {
                FloatingActionButton(
                    onClick = { navController.navigate("record") },
                    containerColor = MaterialTheme.colorScheme.primary,
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Icon(Icons.Filled.Add, contentDescription = "记账")
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = BottomNavItem.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(BottomNavItem.Home.route) { HomeScreen(navController) }
            composable("record") { RecordScreen(navController) }
            composable("record/{transactionId}") { backStackEntry ->
                val transactionId = backStackEntry.arguments?.getString("transactionId")?.toLongOrNull()
                RecordScreen(navController, transactionId)
            }
            composable(BottomNavItem.Transactions.route) { TransactionsScreen(navController) }
            composable(BottomNavItem.Statistics.route) { StatisticsScreen(navController) }
            composable(BottomNavItem.Settings.route) { SettingsScreen(navController) }
            composable("categories") { CategoriesScreen(navController) }
        }
    }
}
