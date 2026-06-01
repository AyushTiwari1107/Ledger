package com.smartkargo.myapplication.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.smartkargo.myapplication.presentation.screen.alert.AlertScreen
import com.smartkargo.myapplication.presentation.screen.analytics.AnalyticsScreen
import com.smartkargo.myapplication.presentation.screen.auth.AuthScreen
import com.smartkargo.myapplication.presentation.screen.calendar.CalendarScreen
import com.smartkargo.myapplication.presentation.screen.dashboard.DashboardScreen
import com.smartkargo.myapplication.presentation.screen.export.ExportScreen
import com.smartkargo.myapplication.presentation.screen.search.SearchScreen
import com.smartkargo.myapplication.presentation.screen.settings.SettingsScreen
import com.smartkargo.myapplication.presentation.screen.transaction.AddTransactionScreen
import com.smartkargo.myapplication.presentation.screen.transaction.EditTransactionScreen

@Composable
fun NavGraph(navController: NavHostController) {
    NavHost(navController = navController, startDestination = Screen.Auth.route) {
        composable(Screen.Auth.route) {
            AuthScreen(onAuthenticated = {
                navController.navigate(Screen.Dashboard.route) {
                    popUpTo(Screen.Auth.route) { inclusive = true }
                }
            })
        }
        composable(Screen.Dashboard.route) {
            DashboardScreen(
                onAddTransaction = { navController.navigate(Screen.AddTransaction.route) },
                onEditTransaction = { id -> navController.navigate(Screen.EditTransaction.createRoute(id)) },
                onAnalytics = { navController.navigate(Screen.Analytics.route) },
                onSearch = { navController.navigate(Screen.Search.route) },
                onSettings = { navController.navigate(Screen.Settings.route) },
                onExport = { navController.navigate(Screen.Export.route) },
                onAlerts = { navController.navigate(Screen.Alerts.route) },
                onCalendar = { navController.navigate(Screen.Calendar.route) }
            )
        }
        composable(Screen.AddTransaction.route) {
            AddTransactionScreen(onBack = { navController.popBackStack() })
        }
        composable(
            Screen.EditTransaction.route,
            arguments = listOf(navArgument("id") { type = NavType.LongType })
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getLong("id") ?: 0L
            EditTransactionScreen(transactionId = id, onBack = { navController.popBackStack() })
        }
        composable(Screen.Analytics.route) {
            AnalyticsScreen(onBack = { navController.popBackStack() })
        }
        composable(Screen.Search.route) {
            SearchScreen(onBack = { navController.popBackStack() })
        }
        composable(Screen.Settings.route) {
            SettingsScreen(onBack = { navController.popBackStack() })
        }
        composable(Screen.Export.route) {
            ExportScreen(onBack = { navController.popBackStack() })
        }
        composable(Screen.Alerts.route) {
            AlertScreen(onBack = { navController.popBackStack() })
        }
        composable(Screen.Calendar.route) {
            CalendarScreen(onBack = { navController.popBackStack() })
        }
    }
}

