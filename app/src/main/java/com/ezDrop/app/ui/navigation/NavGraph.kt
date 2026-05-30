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

object Routes {
    const val LOGIN = "login"
    const val REGISTER = "register"
    const val HOME = "home"
    const val CASE_DETAIL = "case/{caseId}"
}

@Composable
fun NavGraph(
    navController: NavHostController,
    startDestination: String
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Routes.LOGIN) {
            val authViewModel: AuthViewModel = viewModel()
            LoginScreen(
                viewModel = authViewModel,
                onLoginSuccess = {
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
            val mainViewModel: MainViewModel = viewModel()
            HomeScreen(
                viewModel = mainViewModel,
                onLogout = {
                    mainViewModel.logout()
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(Routes.HOME) { inclusive = true }
                    }
                },
                onNavigateToCase = { caseId ->
                    navController.navigate("case/$caseId")
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
                onBack = { navController.popBackStack() }
            )
        }
    }
}
