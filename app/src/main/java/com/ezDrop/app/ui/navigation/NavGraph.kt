package com.ezDrop.app.ui.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.ezDrop.app.viewmodel.AuthViewModel
import com.ezDrop.app.viewmodel.MainViewModel
import com.ezDrop.app.ui.screen.auth.LoginScreen
import com.ezDrop.app.ui.screen.auth.RegisterScreen
import com.ezDrop.app.ui.screen.cases.CaseDetailScreen
import com.ezDrop.app.ui.screen.home.HomeScreen
import com.ezDrop.app.ui.screen.inventory.InventoryDetailScreen
import com.ezDrop.app.ui.screen.splash.SplashScreen

object Routes {
    const val SPLASH = "splash"
    const val LOGIN = "login"
    const val REGISTER = "register"
    const val HOME = "home"
    const val CASE_DETAIL = "case_detail/{caseId}"
    const val INVENTORY_DETAIL = "inventory_detail/{inventoryId}"

    fun caseDetail(caseId: Long) = "case_detail/$caseId"
    fun inventoryDetail(inventoryId: Long) = "inventory_detail/$inventoryId"
}

@Composable
fun NavGraph(
    navController: NavHostController,
    startDestination: String
) {
    val mainViewModel: MainViewModel = viewModel()

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Routes.SPLASH) {
            SplashScreen(
                onLoaded = {
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(Routes.SPLASH) { inclusive = true }
                    }
                }
            )
        }
        composable(Routes.LOGIN) {
            val authViewModel: AuthViewModel = viewModel()
            LoginScreen(
                viewModel = authViewModel,
                onLoginSuccess = {
                    mainViewModel.refreshUser()
                    navController.navigate(Routes.HOME) {
                        popUpTo(Routes.LOGIN) { inclusive = true }
                    }
                },
                onNavigateToRegister = {
                    navController.navigate(Routes.REGISTER)
                }
            )
        }
        composable(Routes.REGISTER) {
            val authViewModel: AuthViewModel = viewModel()
            RegisterScreen(
                viewModel = authViewModel,
                onRegisterSuccess = {
                    mainViewModel.refreshUser()
                    navController.navigate(Routes.HOME) {
                        popUpTo(Routes.LOGIN) { inclusive = true }
                    }
                },
                onNavigateToLogin = {
                    navController.popBackStack()
                }
            )
        }
        composable(Routes.HOME) {
            HomeScreen(
                viewModel = mainViewModel,
                onLogout = {
                    mainViewModel.logout()
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(Routes.HOME) { inclusive = true }
                    }
                },
                onNavigateToCase = { caseId ->
                    navController.navigate(Routes.caseDetail(caseId))
                },
                onNavigateToInventoryDetail = { inventoryId ->
                    navController.navigate(Routes.inventoryDetail(inventoryId))
                }
            )
        }
        composable(
            route = Routes.CASE_DETAIL,
            arguments = listOf(navArgument("caseId") { type = NavType.LongType })
        ) { backStackEntry ->
            val caseId = backStackEntry.arguments?.getLong("caseId") ?: return@composable
            CaseDetailScreen(
                caseId = caseId,
                onBack = { navController.popBackStack() },
                onBalanceChanged = { mainViewModel.refreshUser() }
            )
        }
        composable(
            route = Routes.INVENTORY_DETAIL,
            arguments = listOf(navArgument("inventoryId") { type = NavType.LongType })
        ) { backStackEntry ->
            val inventoryId = backStackEntry.arguments?.getLong("inventoryId") ?: return@composable
            InventoryDetailScreen(
                inventoryId = inventoryId,
                onBack = { navController.popBackStack() },
                onSold = {
                    mainViewModel.refreshUser()
                    navController.popBackStack()
                }
            )
        }
    }
}
