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
        startDestination = Routes.HOME
    ) {
        composable(Routes.HOME) {
            HomeScreen(
                onDevicesClick = { navController.navigate(Routes.DEVICES) }
            )
        }

        composable(Routes.DEVICES) {
            DeviceListScreen(
                onDeviceClick = { id -> navController.navigate(Routes.deviceDetail(id)) },
                onBack = { navController.popBackStack() }
            )
        }

        composable(
            route = Routes.DEVICE_DETAIL,
            arguments = listOf(navArgument("deviceId") { type = NavType.StringType })
        ) { backStackEntry ->
            val deviceId = backStackEntry.arguments?.getString("deviceId").orEmpty()
            DeviceDetailScreen(
                deviceId = deviceId,
                onHistoryClick = { navController.navigate(Routes.deviceHistory(deviceId)) },
                onSettingsClick = { navController.navigate(Routes.deviceSettings(deviceId)) },
                onBack = { navController.popBackStack() }
            )
        }

        composable(
            route = Routes.DEVICE_HISTORY,
            arguments = listOf(navArgument("deviceId") { type = NavType.StringType })
        ) { backStackEntry ->
            val deviceId = backStackEntry.arguments?.getString("deviceId").orEmpty()
            SimplePlaceholderScreen(
                title = "Device History for $deviceId",
                onBack = { navController.popBackStack() }
            )
        }

        composable(
            route = Routes.DEVICE_SETTINGS,
            arguments = listOf(navArgument("deviceId") { type = NavType.StringType })
        ) { backStackEntry ->
            val deviceId = backStackEntry.arguments?.getString("deviceId").orEmpty()
            SimplePlaceholderScreen(
                title = "Settings for $deviceId",
                onBack = { navController.popBackStack() }
            )
        }
    }
}
