package com.meet.navigationdrawerjc.navigation

/**
 * @author Coding Meet
 * Created 17-01-2024 at 02:20 pm, About US, About Project, Settings, Signin As a Guest
 */

sealed class Screens(var route: String) {

    object  Login : Screens("login")
    object  Home : Screens("About US")
    object  Profile : Screens("About Project")
    object  Setting : Screens("Settings")
}