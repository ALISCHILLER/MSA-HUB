package com.msa.msahub.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.msa.msahub.features.devices.presentation.screens.DeviceDetailScreen
import com.msa.msahub.features.devices.presentation.screens.DeviceListScreen
import com.msa.msahub.features.home.presentation.HomeScreen

@Composable
fun AppNavGraph() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Routes.home
    ) {
        composable(Routes.home) {
            HomeScreen(
                onDevicesClick = { navController.navigate(Routes.devices) }
            )
        }

        composable(Routes.devices) {
            DeviceListScreen(
                onDeviceClick = { id -> navController.navigate(Routes.deviceDetail(id)) },
                onBack = { navController.popBackStack() }
            )
        }

        composable(
            route = Routes.device_detail,
            arguments = listOf(navArgument("deviceId") { type = NavType.StringType })
        ) { backStackEntry ->
            val deviceId = backStackEntry.arguments?.getString("deviceId") ?: ""
            DeviceDetailScreen(
                deviceId = deviceId,
                onBack = { navController.popBackStack() }
            )
        }

        composable(
            route = Routes.device_history,
            arguments = listOf(navArgument("deviceId") { type = NavType.StringType })
        ) {
            SimplePlaceholderScreen(title = "Device History", onBack = { navController.popBackStack() })
        }

        composable(
            route = Routes.device_settings,
            arguments = listOf(navArgument("deviceId") { type = NavType.StringType })
        ) {
            SimplePlaceholderScreen(title = "Device Settings", onBack = { navController.popBackStack() })
        }
    }
}
