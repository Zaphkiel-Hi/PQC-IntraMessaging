package org.niklasunrau.pqcmessenger.presentation.auth.viewmodel

import org.niklasunrau.pqcmessenger.presentation.util.UiText

data class AuthUIState (
    var isLoading: Boolean = false,

    var username: String = "",
    var email: String = "",
    var password: String = "",
    var confirmPassword: String = "",

    var usernameError: UiText = UiText.DynamicString(""),
    var emailError: UiText = UiText.DynamicString(""),
    var passwordError: UiText = UiText.DynamicString(""),
    var confirmPasswordError: UiText = UiText.DynamicString("")
)