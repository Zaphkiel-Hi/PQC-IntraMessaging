package org.niklasunrau.pqcmessenger.domain.util

object Validator {
    fun validateUsername(username: String): Boolean {
        return username.isNotEmpty()
    }

    fun validateEmail(email: String): Boolean {
        return email.isNotEmpty()
    }

    fun validatePassword(password: String): Boolean {
        return password.length >= 6 && password.isNotBlank()
    }

    fun validatePasswordIdentical(password: String, confirmPassword: String): Boolean {
        return password.isNotEmpty() && password == confirmPassword
    }

}