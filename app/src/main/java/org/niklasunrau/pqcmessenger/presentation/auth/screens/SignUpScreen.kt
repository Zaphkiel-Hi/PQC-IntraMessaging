package org.niklasunrau.pqcmessenger.presentation.auth.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import org.niklasunrau.pqcmessenger.R
import org.niklasunrau.pqcmessenger.presentation.auth.viewmodel.AuthViewModel
import org.niklasunrau.pqcmessenger.presentation.composables.AutoSizeText
import org.niklasunrau.pqcmessenger.presentation.composables.CustomCircularProgress
import org.niklasunrau.pqcmessenger.presentation.composables.CustomClickableText
import org.niklasunrau.pqcmessenger.presentation.composables.CustomFilledButton
import org.niklasunrau.pqcmessenger.presentation.composables.LogInTextField
import org.niklasunrau.pqcmessenger.presentation.util.Dimens
import org.niklasunrau.pqcmessenger.presentation.util.Dimens.LargePadding

@Composable
fun SignUpScreen(
    onNavigateToStart: () -> Unit,
    onNavigateToLogIn: () -> Unit,
    viewModel: AuthViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier.padding(Dimens.MediumPadding)
    ) {

        IconButton(onClick = {
            onNavigateToStart()
        }) {
            Icon(
                imageVector = Icons.Filled.ArrowBack,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.size(30.dp)
            )
        }
        Spacer(modifier = Modifier.height(LargePadding))
        AutoSizeText(
            text = stringResource(id = R.string.create_account),
            style = MaterialTheme.typography.displayMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(Dimens.MediumPadding))
        LogInTextField(label = stringResource(id = R.string.username),
            value = uiState.username,
            icon = Icons.Outlined.Person,
            errorText = uiState.usernameError.asString(),
            onValueChanged = {
                viewModel.onUsernameChange(it)
            })
        LogInTextField(label = stringResource(id = R.string.email),
            value = uiState.email,
            icon = Icons.Outlined.Email,
            type = KeyboardType.Email,
            errorText = uiState.emailError.asString(),
            onValueChanged = {
                viewModel.onEmailChange(it)
            })
        LogInTextField(label = stringResource(id = R.string.password),
            value = uiState.password,
            icon = Icons.Outlined.Lock,
            type = KeyboardType.Password,
            errorText = uiState.passwordError.asString(),
            onValueChanged = {
                viewModel.onPasswordChange(it)
            })
        LogInTextField(label = stringResource(id = R.string.confirm_password),
            value = uiState.confirmPassword,
            icon = Icons.Outlined.Lock,
            type = KeyboardType.Password,
            isLast = true,
            errorText = uiState.confirmPasswordError.asString(),
            onValueChanged = {
                viewModel.onConfirmPasswordChange(it)
            })
        Spacer(modifier = Modifier.height(Dimens.SmallPadding))
        CustomFilledButton(text = stringResource(id = R.string.signup),
            modifier = Modifier.fillMaxWidth(),
            onClicked = {
                viewModel.signup(
                    onNavigateToLogIn
                )
            })
        Spacer(modifier = Modifier.weight(1f))

        CustomClickableText(
            modifier = Modifier.align(Alignment.CenterHorizontally), textBlocks = listOf(
                stringResource(id = R.string.already_account) + " ", stringResource(id = R.string.login)
            ), onClicks = listOf(onNavigateToLogIn)
        )
    }
    CustomCircularProgress(isDisplayed = uiState.isLoading)
}
//
//@Preview(showBackground = true)
//@Composable
//private fun Preview() {
//    SignUpScreen(
//        onNavigateToStart = { },
//        onNavigateToMain = { },
//        onNavigateToLogIn = {},
//        viewModel = AuthViewModel(AuthRepositoryTest(), UserRepositoryTest())
//    )
//}