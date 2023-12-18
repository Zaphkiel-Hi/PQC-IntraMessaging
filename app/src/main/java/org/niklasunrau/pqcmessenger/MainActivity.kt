package org.niklasunrau.pqcmessenger

import android.os.Bundle
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
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
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
                val authViewModel = hiltViewModel<AuthViewModel>()

                fun NavOptionsBuilder.popUpToTop(navController: NavController) {
                    popUpTo(navController.currentBackStackEntry?.destination?.route ?: return) {
                        inclusive = true
                    }
                }

                fun navToMain() {
                    navController.navigate(Route.Main.name) {
                        popUpTo(0)
                    }
                }
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    NavHost(
                        navController = navController,
                        startDestination = if (authViewModel.isUserSignedIn()) Route.Main.name else Route.Auth.name,
                        modifier = Modifier.fillMaxSize(),

                        ) {
                        navigation(
                            startDestination = Route.Start.name,
                            route = Route.Auth.name
                        ) {
                            composable(Route.Start.name) {
                                StartScreen(
                                    onNavigateToLogIn = { navController.navigate(Route.LogIn.name) },
                                    onNavigateToSignUp = { navController.navigate(Route.SignUp.name) },
                                )
                            }
                            composable(Route.LogIn.name) {
                                LogInScreen(
                                    onNavigateToStart = { navController.navigate(Route.Start.name) },
                                    onNavigateToResetPassword = { navController.navigate(Route.ResetPassword.name) },
                                    onNavigateToSignUp = { navController.navigate(Route.SignUp.name) },
                                    onNavigateToMain = { navToMain() },
                                    viewModel = it.sharedViewModel<AuthViewModel>(navController = navController)

                                )
                            }
                            composable(Route.SignUp.name) {
                                SignUpScreen(
                                    onNavigateToStart = { navController.navigate(Route.Start.name) },
                                    onNavigateToLogIn = { navController.navigate(Route.LogIn.name) },
                                    onNavigateToMain = { navToMain() },
                                    viewModel = it.sharedViewModel<AuthViewModel>(navController = navController)
                                )
                            }
                            composable(Route.ResetPassword.name) {
                                ResetPasswordScreen()
                            }
                        }
                        navigation(
                            startDestination = Route.Chats.name,
                            route = Route.Main.name
                        ) {
                            composable(Route.Chats.name) {
                                ChatsScreen(
                                    onNavigateToRoute = { route -> navController.navigate(route.name) },
                                    onNavigateToAuth = {
                                        navController.navigate(Route.Auth.name) {
                                            popUpTo(0)
                                        }
                                    },
                                    onNavigateToSingleChat = { chatId ->
                                        navController.navigate(Route.SingleChat.name + "/" + chatId)
                                    },
                                    viewModel = it.sharedViewModel<MainViewModel>(navController = navController)
                                )
                            }
                            composable(Route.Profile.name) {
                                ProfileScreen(
                                    onNavigateToRoute = { route -> navController.navigate(route.name) },
                                    viewModel = it.sharedViewModel<MainViewModel>(navController = navController)
                                )
                            }
                            composable(Route.Settings.name) {
                                SettingsScreen(
                                    onNavigateToRoute = { route -> navController.navigate(route.name) },
                                    viewModel = it.sharedViewModel<MainViewModel>(navController = navController)
                                )

                            }
                            composable(Route.Contact.name) {
                                ContactScreen(
                                    onNavigateToRoute = { route -> navController.navigate(route.name) },
                                    viewModel = it.sharedViewModel<MainViewModel>(navController = navController)
                                )
                            }
                            composable(
                                Route.SingleChat.name + "/{chatId}",
                                arguments = listOf(navArgument("chatId") {
                                    type = NavType.StringType
                                    defaultValue = ""
                                    nullable = false
                                })
                            ) { navBackStackEntry ->
                                val chatId = navBackStackEntry.arguments?.getString("chatId")
                                chatId?.let {
                                    SingleChatScreen(chatId = it,
                                        onNavigateToChats = { /*TODO*/ })
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