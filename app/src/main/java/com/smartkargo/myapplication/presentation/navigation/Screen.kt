package com.smartkargo.myapplication.presentation.navigation

sealed class Screen(val route: String) {
    data object Auth : Screen("auth")
    data object Dashboard : Screen("dashboard")
    data object AddTransaction : Screen("add_transaction")
    data object EditTransaction : Screen("edit_transaction/{id}") {
        fun createRoute(id: Long) = "edit_transaction/$id"
    }
    data object Analytics : Screen("analytics")
    data object Search : Screen("search")
    data object Settings : Screen("settings")
    data object Export : Screen("export")
    data object Alerts : Screen("alerts")
    data object Calendar : Screen("calendar")
}
