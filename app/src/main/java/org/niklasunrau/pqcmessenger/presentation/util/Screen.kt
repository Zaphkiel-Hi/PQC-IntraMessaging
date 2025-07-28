package org.niklasunrau.pqcmessenger.presentation.util

import org.niklasunrau.pqcmessenger.R

sealed class Screen(val route: String, val title: Int = 0) {
    data object Auth : Screen ("auth") {
        data object Start : Screen("start")
        data object LogIn : Screen("login")
        data object SignUp : Screen("signup")
        data object Reset : Screen("reset")
    }

    data object Main : Screen ("main?password={password}") {
        fun createRoute(password: String) = "main?password=$password"
        data object Chats : Screen("chats", R.string.app_name)
        data object Profile : Screen("profile", R.string.profile)
        data object Settings : Screen("settings", R.string.settings)
        data object Contact : Screen("contact", R.string.contact)
        data object SingleChat : Screen("singleChat?chatId={chatId}"){
            fun createRoute(chatId: String) = "singleChat?chatId=$chatId"
        }

    }

}