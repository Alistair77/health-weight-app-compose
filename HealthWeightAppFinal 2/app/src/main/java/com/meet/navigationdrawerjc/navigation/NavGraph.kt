package com.meet.navigationdrawerjc.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.meet.navigationdrawerjc.screens.HomeScreen
import com.meet.navigationdrawerjc.screens.LoginScreen
import com.meet.navigationdrawerjc.screens.NotificationScreen
import com.meet.navigationdrawerjc.screens.ProfileScreen
import com.meet.navigationdrawerjc.screens.SettingScreen
import com.meet.navigationdrawerjc.viewmodel.SettingsViewModel
import com.meet.navigationdrawerjc.viewmodel.SettingsViewModelFactory

/**
 * @author Coding Meet
 * Created 17-01-2024 at 02:22 pm
 */

@Composable
fun SetUpNavGraph(
    navController: NavHostController,
    innerPadding: PaddingValues
) {
    NavHost(navController = navController,
        startDestination = Screens.Login.route){
        composable(Screens.Login.route) {
            LoginScreen(navController = navController)
        }
        composable(Screens.Home.route){
            HomeScreen(innerPadding = innerPadding)
        }
        composable(Screens.Profile.route){
            ProfileScreen(innerPadding = innerPadding)
        }
        composable(Screens.Setting.route) {
            val context = LocalContext.current
            val viewModel: SettingsViewModel = viewModel(factory = SettingsViewModelFactory(context))
            SettingScreen(viewModel = viewModel, innerPadding = innerPadding)
        }
    }
}