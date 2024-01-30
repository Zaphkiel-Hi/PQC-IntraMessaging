package org.niklasunrau.pqcmessenger.presentation.auth.viewmodel

import org.niklasunrau.pqcmessenger.presentation.util.UiText

data class AuthUIState (
    val isLoading: Boolean = false,

    val username: String = "",
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",

    val usernameError: UiText = UiText.DynamicString(""),
    val emailError: UiText = UiText.DynamicString(""),
    val passwordError: UiText = UiText.DynamicString(""),
    val confirmPasswordError: UiText = UiText.DynamicString("")
)