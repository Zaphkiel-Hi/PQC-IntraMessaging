package org.niklasunrau.pqcmessenger.presentation.auth.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.niklasunrau.pqcmessenger.R
import org.niklasunrau.pqcmessenger.domain.model.User
import org.niklasunrau.pqcmessenger.domain.repository.AuthRepository
import org.niklasunrau.pqcmessenger.domain.repository.UserRepository
import org.niklasunrau.pqcmessenger.domain.util.Status
import org.niklasunrau.pqcmessenger.presentation.util.UiText
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository

) : ViewModel() {
    private val _uiState = MutableStateFlow(AuthUIState())
    val uiState = _uiState.asStateFlow()

    fun isUserSignedIn(): Boolean {
        return authRepository.isUserSignedIn
    }

    fun onUsernameChange(username: String) {
        _uiState.update { it.copy(username = username, usernameError = UiText.DynamicString("")) }

    }

    fun onEmailChange(email: String) {
        _uiState.update { it.copy(email = email, emailError = UiText.DynamicString("")) }
    }

    fun onPasswordChange(password: String) {
        _uiState.update { it.copy(password = password, passwordError = UiText.DynamicString("")) }
    }

    fun onConfirmPasswordChange(confirmPassword: String) {
        _uiState.update { it.copy(confirmPassword = confirmPassword, confirmPasswordError = UiText.DynamicString("")) }
    }


    fun login(
        onNavigateToHome: () -> Unit
    ) {
        val email = uiState.value.email
        val password = uiState.value.password
        _uiState.update { it.copy(isLoading = true) }

        if (!isTextFieldsValid("login")) {
            _uiState.update { it.copy(isLoading = false) }
            return
        }

        viewModelScope.launch {
            authRepository.login(email, password).collectLatest { result ->
                when (result) {
                    is Status.Loading -> {
                    }

                    is Status.Error -> {
                        _uiState.update { it.copy(isLoading = false) }
                        when (result.error) {
                            is FirebaseAuthInvalidCredentialsException ->
                                _uiState.update { currentState ->
                                    currentState.copy(
                                        usernameError = UiText.StringResource(R.string.invalid_credentials),
                                        passwordError = UiText.StringResource(R.string.invalid_credentials)
                                    )
                                }
                        }
                    }

                    is Status.Success -> {
                        _uiState.update { it.copy(isLoading = false) }
                        onNavigateToHome()
                    }
                }
            }
        }
    }

    private fun isTextFieldsValid(type: String): Boolean {
        val usernameValid = _uiState.value.username.isNotBlank()
        val emailValid = _uiState.value.email.isNotBlank()
        val passwordValid = _uiState.value.password.isNotBlank()
        val confirmPasswordValid = _uiState.value.confirmPassword.isNotBlank()
        _uiState.update { currentState ->
            currentState.copy(
                usernameError = if (usernameValid) currentState.usernameError else UiText.StringResource(R.string.cannot_be_empty),
                emailError = if (emailValid) currentState.emailError else UiText.StringResource(R.string.cannot_be_empty),
                passwordError = if (passwordValid) currentState.passwordError else UiText.StringResource(R.string.cannot_be_empty),
                confirmPasswordError = if (confirmPasswordValid) currentState.confirmPasswordError else UiText.StringResource(
                    R.string.cannot_be_empty
                )
            )
        }

        return if (type == "login") (emailValid && passwordValid) else (usernameValid && emailValid && passwordValid && confirmPasswordValid)
    }

    fun signup(
        onNavigateToHome: () -> Unit
    ) {
        val username = _uiState.value.username
        val email = _uiState.value.email
        val password = _uiState.value.password
        val confirmPassword = _uiState.value.confirmPassword

        _uiState.update { it.copy(isLoading = true) }

        if (!isTextFieldsValid("signup")) {
            _uiState.update { it.copy(isLoading = false) }
            return
        }

        if (password != confirmPassword) {
            _uiState.update { currentState ->
                currentState.copy(
                    confirmPasswordError = UiText.StringResource(R.string.password_not_identical),
                    isLoading = false
                )
            }
            return
        }

        viewModelScope.launch {
            if (userRepository.isUsernameInUse(username)) {
                _uiState.update { currentState ->
                    currentState.copy(
                        usernameError = UiText.StringResource(R.string.username_in_use),
                        isLoading = false
                    )
                }
                this.cancel()
            }

            authRepository.signup(email, password).collectLatest { result ->
                when (result) {
                    is Status.Loading -> {}
                    is Status.Error -> {
                        _uiState.update { it.copy(isLoading = false) }
                        when (result.error) {
                            is FirebaseAuthWeakPasswordException -> {
                                _uiState.update { currentState ->
                                    currentState.copy(passwordError = UiText.StringResource(R.string.password_not_valid))
                                }
                            }

                            is FirebaseAuthInvalidCredentialsException -> {
                                _uiState.update { currentState ->
                                    currentState.copy(emailError = UiText.StringResource(R.string.email_not_valid))
                                }
                            }

                            is FirebaseAuthUserCollisionException -> {
                                _uiState.update { currentState ->
                                    currentState.copy(emailError = UiText.StringResource(R.string.email_in_use))
                                }
                            }
                        }
                    }


                    is Status.Success -> {
                        userRepository.createUser(
                            User(
                                id = authRepository.currentUserId,
                                email = email,
                                username = username
                            )
                        )
                        _uiState.update { it.copy(isLoading = false) }
                        onNavigateToHome()
                    }

                }
            }

        }
    }
}
