package com.example.accountingapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
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
                AccountingApp()
            }
        }
    }
}

sealed class BottomNavItem(val route: String, val title: String, val icon: ImageVector) {
    object Home : BottomNavItem("home", "首页", Icons.Filled.Home)
    object Record : BottomNavItem("record", "记账", Icons.Filled.Add)
    object Transactions : BottomNavItem("transactions", "账单", Icons.Filled.List)
    object Statistics : BottomNavItem("statistics", "统计", Icons.Filled.BarChart)
    object Settings : BottomNavItem("settings", "设置", Icons.Filled.Settings)
}

val bottomNavItems = listOf(
    BottomNavItem.Home,
    BottomNavItem.Record,
    BottomNavItem.Transactions,
    BottomNavItem.Statistics,
    BottomNavItem.Settings
)

@Composable
fun AccountingApp() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Scaffold(
        bottomBar = {
            NavigationBar {
                bottomNavItems.forEach { item ->
                    NavigationBarItem(
                        icon = { Icon(item.icon, contentDescription = item.title) },
                        label = { Text(item.title) },
                        selected = currentRoute == item.route,
                        onClick = {
                            navController.navigate(item.route) {
                                popUpTo(navController.graph.startDestinationId) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        },
        floatingActionButton = {
            if (currentRoute == BottomNavItem.Home.route) {
                FloatingActionButton(
                    onClick = { navController.navigate(BottomNavItem.Record.route) },
                    containerColor = MaterialTheme.colorScheme.primary
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
            composable(BottomNavItem.Record.route) { RecordScreen(navController) }
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
