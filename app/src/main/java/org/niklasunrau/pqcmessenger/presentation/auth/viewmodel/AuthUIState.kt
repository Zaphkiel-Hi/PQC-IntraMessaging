package org.niklasunrau.pqcmessenger.presentation.auth.viewmodel

data class AuthUIState (
    var isLoading: Boolean = false,

    var username: String = "",
    var email: String = "",
    var password: String = "",
    var confirmPassword: String = "",

    var usernameError: Boolean = false,
    var emailError: Boolean = false,
    var passwordError: Boolean = false,
    var confirmPasswordError: Boolean = false
)