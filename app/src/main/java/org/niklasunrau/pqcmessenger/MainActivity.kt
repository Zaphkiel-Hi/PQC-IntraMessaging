package org.niklasunrau.pqcmessenger

import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.NavOptionsBuilder
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.navigation
import dagger.hilt.android.AndroidEntryPoint
import org.niklasunrau.pqcmessenger.domain.util.Route
import org.niklasunrau.pqcmessenger.presentation.auth.screens.LogInScreen
import org.niklasunrau.pqcmessenger.presentation.auth.screens.ResetPasswordScreen
import org.niklasunrau.pqcmessenger.presentation.auth.screens.SignUpScreen
import org.niklasunrau.pqcmessenger.presentation.auth.screens.StartScreen
import org.niklasunrau.pqcmessenger.presentation.auth.viewmodel.AuthViewModel
import org.niklasunrau.pqcmessenger.presentation.main.screens.ChatsScreen
import org.niklasunrau.pqcmessenger.presentation.main.screens.ContactScreen
import org.niklasunrau.pqcmessenger.presentation.main.screens.ProfileScreen
import org.niklasunrau.pqcmessenger.presentation.main.screens.SettingsScreen
import org.niklasunrau.pqcmessenger.presentation.main.screens.SingleChatScreen
import org.niklasunrau.pqcmessenger.presentation.main.viewmodel.MainViewModel
import org.niklasunrau.pqcmessenger.theme.MessengerTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        installSplashScreen()
        setContent {
            MessengerTheme {
                val navController = rememberNavController()
                val appViewModel = hiltViewModel<AppViewModel>()
                appViewModel.logoutPotentialUser()

                fun NavOptionsBuilder.popUpToTop(navController: NavController) {
                    popUpTo(navController.currentBackStackEntry?.destination?.route ?: return) {
                        inclusive = true
                    }
                }

                fun navigateTo(
                    route: Route, withPopUp: Boolean = false, argumentName: String? = null, argumentValue: String = ""
                ) {
                    val argument = if (argumentName != null) {
                        "?$argumentName=$argumentValue"
                    } else ""
                    navController.navigate(route.name + argument) {
                        if (withPopUp) popUpToTop(navController)
                    }
                }
                Surface(
                    modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.surface
                ) {
                    NavHost(
                        navController = navController,
                        startDestination = Route.Auth.name,
                        modifier = Modifier.fillMaxSize(),

                        ) {
                        navigation(
                            route = Route.Auth.name,
                            startDestination = Route.Start.name
                        ) {
                            composable(Route.Start.name) {
                                StartScreen(
                                    onNavigateToLogIn = { navigateTo(Route.LogIn) },
                                    onNavigateToSignUp = { navigateTo(Route.SignUp) },
                                    viewModel = it.sharedViewModel<AuthViewModel>(navController = navController)

                                )
                            }
                            composable(Route.LogIn.name) {
                                LogInScreen(
                                    onNavigateToStart = { navigateTo(Route.Start) },
                                    onNavigateToResetPassword = { navigateTo(Route.ResetPassword) },
                                    onNavigateToSignUp = { navigateTo(Route.SignUp) },
                                    onNavigateToMain = { password ->
                                        navigateTo(Route.Main, true, "password", password)
                                        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)

                                    },
                                    viewModel = it.sharedViewModel<AuthViewModel>(navController = navController)

                                )
                            }
                            composable(Route.SignUp.name) {
                                SignUpScreen(
                                    onNavigateToStart = { navigateTo(Route.Start) },
                                    onNavigateToLogIn = { navigateTo(Route.LogIn) },
                                    onNavigateToMain = { password ->
                                        navigateTo(Route.Main, true, "password", password)
                                        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
                                    },
                                    viewModel = it.sharedViewModel<AuthViewModel>(navController = navController)
                                )
                            }
                            composable(Route.ResetPassword.name) {
                                ResetPasswordScreen()
                            }

                        }
                        navigation(
                            route = Route.Main.name + "?password={password}",
                            startDestination = Route.Chats.name,
                            arguments = listOf(navArgument("password") { defaultValue = "" })
                        ) {
                            composable(Route.Chats.name) {
                                ChatsScreen(
                                    onNavigateToRoute = { route -> navigateTo(route, true) },
                                    onNavigateToAuth = {
                                        navigateTo(Route.Auth, true)
                                        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)

                                    },
                                    onNavigateToSingleChat = { chatId ->
                                        navigateTo(Route.SingleChat, false, "chatId", chatId)
                                    },
                                    viewModel = it.sharedViewModel<MainViewModel>(navController = navController)
                                )
                            }
                            composable(Route.Profile.name) {
                                ProfileScreen(
                                    onNavigateToRoute = { route -> navigateTo(route, true) },
                                    viewModel = it.sharedViewModel<MainViewModel>(navController = navController)
                                )
                            }
                            composable(Route.Settings.name) {
                                SettingsScreen(
                                    onNavigateToRoute = { route -> navigateTo(route, true) },
                                    viewModel = it.sharedViewModel<MainViewModel>(navController = navController)
                                )

                            }
                            composable(Route.Contact.name) {
                                ContactScreen(
                                    onNavigateToRoute = { route -> navigateTo(route, true) },
                                    viewModel = it.sharedViewModel<MainViewModel>(navController = navController)
                                )
                            }
                            composable(
                                Route.SingleChat.name + "?chatId={chatId}"
                            ) { navBackStackEntry ->
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
    }
}


@Composable
inline fun <reified T : ViewModel> NavBackStackEntry.sharedViewModel(navController: NavController): T {
    val navGraphRoute = destination.parent?.route ?: return hiltViewModel()
    val parentEntry = remember(this) {
        navController.getBackStackEntry(navGraphRoute)
    }
    return hiltViewModel(parentEntry)
}