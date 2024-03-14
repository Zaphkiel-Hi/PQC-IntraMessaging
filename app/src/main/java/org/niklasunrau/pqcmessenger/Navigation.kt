package org.niklasunrau.pqcmessenger

import android.view.Window
import android.view.WindowManager
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.navigation
import org.niklasunrau.pqcmessenger.presentation.auth.screens.LogInScreen
import org.niklasunrau.pqcmessenger.presentation.auth.screens.SignUpScreen
import org.niklasunrau.pqcmessenger.presentation.auth.screens.StartScreen
import org.niklasunrau.pqcmessenger.presentation.auth.viewmodel.AuthViewModel
import org.niklasunrau.pqcmessenger.presentation.composables.CustomNavigationDrawer
import org.niklasunrau.pqcmessenger.presentation.main.screens.ChatsScreen
import org.niklasunrau.pqcmessenger.presentation.main.screens.ContactScreen
import org.niklasunrau.pqcmessenger.presentation.main.screens.ProfileScreen
import org.niklasunrau.pqcmessenger.presentation.main.screens.SettingsScreen
import org.niklasunrau.pqcmessenger.presentation.main.screens.SingleChatScreen
import org.niklasunrau.pqcmessenger.presentation.main.viewmodel.MainViewModel
import org.niklasunrau.pqcmessenger.presentation.util.Screen

@Composable
inline fun <reified T : ViewModel> NavBackStackEntry.sharedViewModel(navController: NavController): T {
    val navGraphRoute = destination.parent?.route ?: return hiltViewModel()
    val parentEntry = remember(this) {
        navController.getBackStackEntry(navGraphRoute)
    }
    return hiltViewModel(parentEntry)
}

@Composable
fun Navigation(window: Window) {
    val navController = rememberNavController()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val appViewModel = hiltViewModel<AppViewModel>()
    appViewModel.logoutPotentialUser()

    fun navigateTo(route: String, withPopUp: Boolean = false) {
        navController.navigate(route) {
            if (withPopUp) popUpTo(0)
        }
    }

    fun navigateTo(screen: Screen, withPopUp: Boolean = false) {
        navigateTo(screen.route, withPopUp)
    }

    Surface(
        modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.surface
    ) {
        CustomNavigationDrawer(drawerState = drawerState,
            navigationItems = appViewModel.navigationItemsList,
            onNavigateToScreen = { navigateTo(it) }) {
            NavHost(
                navController = navController,
                startDestination = Screen.Auth.route,
                modifier = Modifier.fillMaxSize(),
            ) {
                navigation(
                    route = Screen.Auth.route,
                    startDestination = Screen.Auth.Start.route
                ) {
                    composable(Screen.Auth.Start.route) {
                        StartScreen(
                            onNavigateToLogIn = { navigateTo(Screen.Auth.LogIn) },
                            onNavigateToSignUp = { navigateTo(Screen.Auth.SignUp) },
                            viewModel = it.sharedViewModel<AuthViewModel>(navController = navController)
                        )
                    }
                    composable(Screen.Auth.LogIn.route) {
                        LogInScreen(
                            onNavigateToStart = { navController.navigateUp() },
//                                    onNavigateToResetPassword = { navigateTo(Route.ResetPassword) },
                            onNavigateToSignUp = {
                                navController.navigateUp()
                                navigateTo(Screen.Auth.SignUp)
                            },
                            onNavigateToMain = { password ->
                                navigateTo(Screen.Main.createRoute(password), true)
                                window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)

                            },
                            viewModel = it.sharedViewModel<AuthViewModel>(navController = navController)

                        )
                    }
                    composable(Screen.Auth.SignUp.route) {
                        SignUpScreen(
                            onNavigateToStart = { navController.navigateUp() },
                            onNavigateToLogIn = {
                                navController.navigateUp()
                                navigateTo(Screen.Auth.LogIn)
                            },
                            viewModel = it.sharedViewModel<AuthViewModel>(navController = navController)
                        )
                    }
//                            composable(Screen.Auth.ResetPassword.route) {
//                                ResetPasswordScreen()
//                            }

                }
                navigation(
                    route = Screen.Main.route,
                    startDestination = Screen.Main.Chats.route,
                    arguments = listOf(navArgument("password") { defaultValue = "" })
                ) {
                    composable(Screen.Main.Chats.route) {
                        ChatsScreen(
                            drawerState = drawerState,
                            title = stringResource(id = Screen.Main.Chats.title),
                            onNavigateToAuth = {
                                navigateTo(Screen.Auth, true)
                                window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
                            },
                            onNavigateToSingleChat = { chatId ->
                                navigateTo(Screen.Main.SingleChat.createRoute(chatId))
                            },
                            viewModel = it.sharedViewModel<MainViewModel>(navController = navController)
                        )
                    }
                    composable(Screen.Main.Profile.route) {
                        ProfileScreen(
                            drawerState = drawerState,
                            title = stringResource(id = Screen.Main.Profile.title),
                            viewModel = it.sharedViewModel<MainViewModel>(navController = navController)
                        )
                    }
                    composable(Screen.Main.Settings.route) {
                        SettingsScreen(
                            drawerState = drawerState,
                            title = stringResource(id = Screen.Main.Settings.title),
                            viewModel = it.sharedViewModel<MainViewModel>(navController = navController)
                        )
                    }
                    composable(Screen.Main.Contact.route) {
                        ContactScreen(
                            drawerState = drawerState,
                            title = stringResource(id = Screen.Main.Contact.title),
                            viewModel = it.sharedViewModel<MainViewModel>(navController = navController)
                        )
                    }
                    composable(Screen.Main.SingleChat.route) { navBackStackEntry ->
                        val chatId = navBackStackEntry.arguments?.getString("chatId")
                        chatId?.let {
                            SingleChatScreen(
                                chatId = it,
                                onNavigateToChats = { navController.popBackStack() },
                                viewModel = navBackStackEntry.sharedViewModel<MainViewModel>(
                                    navController = navController
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}