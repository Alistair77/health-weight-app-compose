package com.meet.navigationdrawerjc

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.meet.navigationdrawerjc.navigation.NavBarBody
import com.meet.navigationdrawerjc.navigation.NavBarHeader
import com.meet.navigationdrawerjc.navigation.NavigationItem
import com.meet.navigationdrawerjc.navigation.Screens
import com.meet.navigationdrawerjc.navigation.SetUpNavGraph
import com.meet.navigationdrawerjc.ui.theme.NavigationDrawerJetpackComposeTheme
import com.meet.navigationdrawerjc.viewmodel.SettingsViewModel
import com.meet.navigationdrawerjc.viewmodel.SettingsViewModelFactory
import com.meet.navigationdrawerjc.worker.NotificationUtils
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    // Initialize the SettingsViewModel using viewModels delegate
    private val settingsViewModel: SettingsViewModel by viewModels {
        SettingsViewModelFactory(application)
    }

    // Permission request launcher
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            // Permission is granted, proceed with your logic
        } else {
            // Permission is denied, handle the case (e.g., show a message to the user)
        }
    }

    private val requestNotificationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            // Permission granted – continue showing notifications
            Log.d("Permission", "Notification permission granted")
        } else {
            // Permission denied
            Log.d("Permission", "Notification permission denied")
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            NavigationDrawerJetpackComposeTheme {
                val items = listOf(
                    NavigationItem(
                        title = "Home",
                        route = Screens.Home.route,
                        selectedIcon = Icons.Filled.Info,
                        unSelectedIcon = Icons.Outlined.Info,
                    ),
                    NavigationItem(
                        title = "How Mindful Moments Works",
                        route = Screens.Profile.route,
                        selectedIcon = Icons.Filled.Info,
                        unSelectedIcon = Icons.Outlined.Info,
                    ),
                    NavigationItem(
                        title = "Settings",
                        route = Screens.Setting.route,
                        selectedIcon = Icons.Filled.Settings,
                        unSelectedIcon = Icons.Outlined.Settings,
                    ),
                    NavigationItem(
                        title = "Logout",
                        route = Screens.Login.route,
                        selectedIcon = Icons.Filled.ArrowForward,
                        unSelectedIcon = Icons.Filled.ArrowForward,
                    )
                )
                val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
                val scope = rememberCoroutineScope()
                val navController = rememberNavController()
                val context = LocalContext.current
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route

                val selectedIndex = items.indexOfFirst { it.route == currentRoute }
                val topBarTitle = if (selectedIndex != -1) {
                    items[selectedIndex].title
                } else {
                    items.getOrNull(0)?.title ?: "Default Title"
                }
                val isLoginScreen = currentRoute == Screens.Login.route

                if (isLoginScreen) {
                    SetUpNavGraph(navController = navController, innerPadding = PaddingValues(0.dp))
                } else {
                    ModalNavigationDrawer(
                        gesturesEnabled = drawerState.isOpen, drawerContent = {
                            ModalDrawerSheet(

                            ) {
                                NavBarHeader()
                                Spacer(modifier = Modifier.height(8.dp))
                                NavBarBody(
                                    items = items,
                                    currentRoute = currentRoute
                                ) { currentNavigationItem ->
                                    if (currentNavigationItem.route == "share") {
                                        Toast.makeText(context, "Share Clicked", Toast.LENGTH_LONG)
                                            .show()
                                    } else {
                                        navController.navigate(currentNavigationItem.route) {
                                            // Pop up to the start destination of the graph to
                                            // avoid building up a large stack of destinations
                                            // on the back stack as users select items
                                            navController.graph.startDestinationRoute?.let { startDestinationRoute ->
                                                // Pop up to the start destination, clearing the back stack
                                                popUpTo(startDestinationRoute) {
                                                    // Save the state of popped destinations
                                                    saveState = true
                                                }
                                            }

                                            // Configure navigation to avoid multiple instances of the same destination
                                            launchSingleTop = true

                                            // Restore state when re-selecting a previously selected item
                                            restoreState = true
                                        }
                                    }

                                    scope.launch {
                                        drawerState.close()
                                    }
                                }
                            }
                        }, drawerState = drawerState
                    ) {
                        Scaffold(
                            topBar = {
                                TopAppBar(title = {
                                    Text(text = topBarTitle)
                                }, navigationIcon = {
                                    IconButton(onClick = {
                                        scope.launch {
                                            drawerState.open()
                                        }
                                    }) {
                                        Icon(
                                            imageVector = Icons.Default.Menu,
                                            contentDescription = "menu"
                                        )
                                    }
                                })
                            }
                        ) { innerPadding ->
                            SetUpNavGraph(
                                navController = navController,
                                innerPadding = innerPadding
                            )
                        }
                    }
                }
            }
        }

        // Cancel the notification
        NotificationManagerCompat.from(this).cancel(NotificationUtils.NOTIFICATION_ID)

        // Check location permission
        checkAndRequestPermissions()

    }

    private val requestMultiplePermissionsLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            val locationGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false
            val notificationGranted = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                permissions[Manifest.permission.POST_NOTIFICATIONS] ?: false
            } else true // Pre-TIRAMISU, notifications don't need runtime permission

            if (locationGranted) {
                Log.d("Permission", "Location permission granted")
            } else {
                Log.d("Permission", "Location permission denied")
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                if (notificationGranted) {
                    Log.d("Permission", "Notification permission granted")
                } else {
                    Log.d("Permission", "Notification permission denied")
                }
            }
        }

    private fun checkAndRequestPermissions() {
        val permissionsToRequest = mutableListOf<String>()

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED
        ) {
            permissionsToRequest.add(Manifest.permission.ACCESS_FINE_LOCATION)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
            != PackageManager.PERMISSION_GRANTED
        ) {
            permissionsToRequest.add(Manifest.permission.POST_NOTIFICATIONS)
        }

        if (permissionsToRequest.isNotEmpty()) {
            requestMultiplePermissionsLauncher.launch(permissionsToRequest.toTypedArray())
        }
    }
}