package com.taximetro.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.taximetro.ui.home.HomeScreen
import com.taximetro.ui.trip.TripScreen
import com.taximetro.ui.summary.SummaryScreen
import com.taximetro.ui.history.HistoryScreen
import com.taximetro.ui.settings.SettingsScreen
import com.taximetro.ui.receipt.ReceiptScreen

object Routes {
    const val HOME = "home"
    const val TRIP = "trip/{flag}"
    const val SUMMARY = "summary/{tripId}"
    const val HISTORY = "history"
    const val SETTINGS = "settings"
    const val RECEIPT = "receipt/{tripId}"

    fun trip(flag: String) = "trip/$flag"
    fun summary(tripId: Long) = "summary/$tripId"
    fun receipt(tripId: Long) = "receipt/$tripId"
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Routes.HOME) {
        composable(Routes.HOME) {
            HomeScreen(
                onStartTrip = { flag -> navController.navigate(Routes.trip(flag)) },
                onOpenHistory = { navController.navigate(Routes.HISTORY) },
                onOpenSettings = { navController.navigate(Routes.SETTINGS) }
            )
        }
        composable(Routes.TRIP) { backStackEntry ->
            val flag = backStackEntry.arguments?.getString("flag") ?: "BANDEIRA_1"
            TripScreen(
                initialFlag = flag,
                onFinishTrip = { tripId ->
                    navController.navigate(Routes.summary(tripId)) {
                        popUpTo(Routes.HOME)
                    }
                },
                onCancel = { navController.popBackStack() }
            )
        }
        composable(Routes.SUMMARY) { backStackEntry ->
            val tripId = backStackEntry.arguments?.getString("tripId")?.toLongOrNull() ?: 0L
            SummaryScreen(
                tripId = tripId,
                onPrint = { navController.navigate(Routes.receipt(tripId)) },
                onNewTrip = {
                    navController.navigate(Routes.HOME) {
                        popUpTo(Routes.HOME) { inclusive = true }
                    }
                }
            )
        }
        composable(Routes.HISTORY) {
            HistoryScreen(onBack = { navController.popBackStack() })
        }
        composable(Routes.SETTINGS) {
            SettingsScreen(onBack = { navController.popBackStack() })
        }
        composable(Routes.RECEIPT) { backStackEntry ->
            val tripId = backStackEntry.arguments?.getString("tripId")?.toLongOrNull() ?: 0L
            ReceiptScreen(tripId = tripId, onBack = { navController.popBackStack() })
        }
    }
}
