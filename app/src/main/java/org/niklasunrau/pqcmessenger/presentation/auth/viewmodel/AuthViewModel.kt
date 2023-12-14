package org.niklasunrau.pqcmessenger.presentation.auth.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.niklasunrau.pqcmessenger.domain.model.User
import org.niklasunrau.pqcmessenger.domain.repository.AuthRepository
import org.niklasunrau.pqcmessenger.domain.repository.UserRepository
import org.niklasunrau.pqcmessenger.domain.util.Status
import org.niklasunrau.pqcmessenger.domain.util.Validator
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository

) : ViewModel() {
    private val _uiState = MutableStateFlow(AuthUIState())
    val uiState = _uiState.asStateFlow()


    fun checkLoggedIn(
        onNavigateToHome: () -> Unit
    ){
        if(authRepository.currentUserId != "")
            onNavigateToHome()
    }

    private fun resetErrors() {
        _uiState.update { currentState ->
            currentState.copy(
                usernameError = false,
                emailError = false,
                passwordError = false,
                confirmPasswordError = false
            )
        }
    }

    fun onUsernameChange(username: String) {
        _uiState.update { it.copy(username = username) }
        if (uiState.value.usernameError || uiState.value.emailError || uiState.value.passwordError) {
            resetErrors()
        }
    }

    fun onEmailChange(email: String) {
        _uiState.update { it.copy(email = email) }
        if (uiState.value.usernameError || uiState.value.emailError || uiState.value.passwordError) {
            resetErrors()
        }
    }

    fun onPasswordChange(password: String) {
        _uiState.update { it.copy(password = password) }
        if (uiState.value.usernameError || uiState.value.emailError || uiState.value.passwordError) {
            resetErrors()
        }
    }

    fun onConfirmPasswordChange(confirmPassword: String) {
        _uiState.update { it.copy(confirmPassword = confirmPassword) }
        if (uiState.value.usernameError || uiState.value.emailError || uiState.value.passwordError) {
            resetErrors()
        }
    }


    fun login(
        onNavigateToHome: () -> Unit
    ) {
        val email = uiState.value.email
        val password = uiState.value.password

        viewModelScope.launch {
            authRepository.login(email, password).collectLatest { result ->
                when (result) {
                    is Status.Loading -> {
                        _uiState.update { it.copy(isLoading = true) }
                    }

                    is Status.Success -> {
                        _uiState.update { it.copy(isLoading = false) }
                        onNavigateToHome()
                    }

                    is Status.Error -> {
                        _uiState.update { it.copy(isLoading = false) }
                        // TODO
                    }
                }
            }
        }
    }

    private fun validateSignUpData(): Boolean {
        val validUsername = Validator.validateUsername(_uiState.value.username)
        val validEmail = Validator.validateEmail(_uiState.value.email)
        val validPassword = Validator.validatePassword(_uiState.value.password)
        val validConfirmPassword = Validator.validatePassword(_uiState.value.confirmPassword)
        val validIdenticalPassword =
            Validator.validatePasswordIdentical(_uiState.value.password, _uiState.value.confirmPassword)

        _uiState.update { currentState ->
            currentState.copy(
                usernameError = !validUsername,
                emailError = !validEmail,
                passwordError = !validPassword || !validIdenticalPassword,
                confirmPasswordError = !validConfirmPassword || !validIdenticalPassword
            )
        }

        return validUsername && validEmail && validPassword && validConfirmPassword && validIdenticalPassword
    }

    fun signup(
        onNavigateToHome: () -> Unit
    ) {
        val username = _uiState.value.username
        val email = _uiState.value.email
        val password = _uiState.value.password

        if (!validateSignUpData())
            return // TODO

        viewModelScope.launch {
            authRepository.signup(email, password).collectLatest { result ->
                when (result) {
                    is Status.Loading -> {
                        _uiState.update { it.copy(isLoading = true) }
                    }

                    is Status.Success -> {
                        userRepository.createUser(User(authRepository.currentUserId, username = username))
                        _uiState.update { it.copy(isLoading = false) }
                        onNavigateToHome()
                    }

                    is Status.Error -> {
                        _uiState.update { it.copy(isLoading = false) }
                        // TODO
                    }
                }
            }
        }
    }
}
