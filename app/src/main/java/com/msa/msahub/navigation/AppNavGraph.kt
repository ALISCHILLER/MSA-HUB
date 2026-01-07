@file:OptIn(ExperimentalMaterial3Api::class)

package com.msa.msahub.navigation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.msa.msahub.core.ui.components.ConnectionStatusBanner
import com.msa.msahub.features.devices.presentation.screens.*
import com.msa.msahub.features.home.presentation.HomeScreen
import com.msa.msahub.features.scenes.presentation.screens.SceneEditorScreen
import com.msa.msahub.features.scenes.presentation.screens.SceneListScreen
import com.msa.msahub.features.settings.presentation.SettingsScreen

@Composable
fun AppNavGraph() {
    val navController = rememberNavController()

    val topLevel = listOf(
        Triple(Routes.HOME, "Home", Icons.Default.Home),
        Triple(Routes.DEVICES, "Devices", Icons.Default.List),
        Triple(Routes.SCENES, "Scenes", Icons.Default.PlayArrow),
        Triple(Routes.SETTINGS, "Settings", Icons.Default.Settings)
    )

    Scaffold(
        topBar = {
            // نمایش وضعیت اتصال MQTT در بالای تمام صفحات به صورت انیمیشنی
            ConnectionStatusBanner()
        },
        bottomBar = {
            NavigationBar {
                val navBackStackEntry = navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry.value?.destination?.route
                
                topLevel.forEach { (route, label, icon) ->
                    NavigationBarItem(
                        selected = currentRoute == route,
                        onClick = {
                            navController.navigate(route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = { Icon(icon, contentDescription = label) },
                        label = { Text(label) }
                    )
                }
            }
        }
    ) { padding ->
        // استفاده از Column برای اینکه محتوا زیر بنر وضعیت قرار نگیرد
        Column(modifier = Modifier.padding(padding)) {
            NavHost(
                navController = navController,
                startDestination = Routes.HOME,
                modifier = Modifier.weight(1f)
            ) {
                composable(Routes.HOME) {
                    HomeScreen(
                        onDeviceClick = { id -> navController.navigate(Routes.deviceDetail(id)) },
                        onSeeAllDevices = { navController.navigate(Routes.DEVICES) }
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
                    DeviceHistoryScreen(deviceId = deviceId, onBack = { navController.popBackStack() })
                }

                composable(
                    route = Routes.DEVICE_SETTINGS,
                    arguments = listOf(navArgument("deviceId") { type = NavType.StringType })
                ) { backStackEntry ->
                    val deviceId = backStackEntry.arguments?.getString("deviceId").orEmpty()
                    DeviceSettingsScreen(deviceId = deviceId, onBack = { navController.popBackStack() })
                }

                composable(Routes.SCENES) {
                    SceneListScreen(
                        onCreate = { navController.navigate(Routes.sceneEditor("new")) },
                        onEdit = { id -> navController.navigate(Routes.sceneEditor(id)) }
                    )
                }

                composable(
                    route = Routes.SCENE_EDITOR,
                    arguments = listOf(navArgument("sceneId") { type = NavType.StringType })
                ) { backStackEntry ->
                    val sceneId = backStackEntry.arguments?.getString("sceneId")
                    SceneEditorScreen(sceneId = sceneId, onBack = { navController.popBackStack() })
                }

                composable(Routes.SETTINGS) {
                    SettingsScreen()
                }
            }
        }
    }
}
