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
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import org.niklasunrau.pqcmessenger.R
import org.niklasunrau.pqcmessenger.data.test.AuthRepositoryTest
import org.niklasunrau.pqcmessenger.data.test.UserRepositoryTest
import org.niklasunrau.pqcmessenger.presentation.auth.viewmodel.AuthViewModel
import org.niklasunrau.pqcmessenger.presentation.composables.AutoSizeText
import org.niklasunrau.pqcmessenger.presentation.composables.CustomCircularProgress
import org.niklasunrau.pqcmessenger.presentation.composables.CustomClickableText
import org.niklasunrau.pqcmessenger.presentation.composables.CustomFilledButton
import org.niklasunrau.pqcmessenger.presentation.composables.LogInTextField
import org.niklasunrau.pqcmessenger.presentation.util.Dimens.LargePadding
import org.niklasunrau.pqcmessenger.presentation.util.Dimens.MediumPadding

@Composable
fun LogInScreen(
    onNavigateToStart: () -> Unit,
    onNavigateToResetPassword: () -> Unit,
    onNavigateToSignUp: () -> Unit,
    onNavigateToMain: () -> Unit,
    viewModel: AuthViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier.padding(MediumPadding),
    ) {

        IconButton(onClick = {
            onNavigateToStart()
        }) {
            Icon(
                imageVector = Icons.Filled.ArrowBack,
                contentDescription = null,
                tint = Color.Gray,
                modifier = Modifier.size(30.dp)
            )
        }
        Spacer(modifier = Modifier.height(LargePadding))
        AutoSizeText(
            text = stringResource(id = R.string.login),
            style = MaterialTheme.typography.displayMedium
        )
        AutoSizeText(
            text = "Welcome to the Post-Quantum Messenger",
            style = MaterialTheme.typography.bodyLarge
        )
        Spacer(modifier = Modifier.height(MediumPadding))
        LogInTextField(
            label = stringResource(id = R.string.email),
            icon = Icons.Outlined.Email,
            value = uiState.email,
            type = KeyboardType.Email,
            errorText = uiState.emailError.asString(),
            onValueChanged = {
                viewModel.onEmailChange(it)
            }
        )
        LogInTextField(
            label = stringResource(id = R.string.password),
            icon = Icons.Outlined.Lock,
            value = uiState.password,
            type = KeyboardType.Password,
            isLast = true,
            errorText = uiState.passwordError.asString(),
            onValueChanged = {
                viewModel.onPasswordChange(it)
            }
        )
        CustomClickableText(
            textBlocks = listOf(stringResource(id = R.string.reset_password)),
            modifier = Modifier.align(Alignment.End),
            onClicks = listOf(onNavigateToResetPassword),
            firstClickableIsSecond = false
        )
        Spacer(modifier = Modifier.height(LargePadding))
        CustomFilledButton(
            text = stringResource(id = R.string.login),
            modifier = Modifier.fillMaxWidth(),
            onClicked = {
                viewModel.login(onNavigateToMain)
            }
        )
        Spacer(modifier = Modifier.weight(1f))
        CustomClickableText(
            modifier = Modifier.align(Alignment.CenterHorizontally),
            textBlocks = listOf(
                stringResource(id = R.string.no_account) + " ",
                stringResource(id = R.string.signup)
            ),
            onClicks = listOf(onNavigateToSignUp)
        )
    }
    CustomCircularProgress(isDisplayed = uiState.isLoading)
}

@Preview(showBackground = true)
@Composable
private fun Preview() {
    LogInScreen(
        onNavigateToStart = { },
        onNavigateToResetPassword = { },
        onNavigateToSignUp = { },
        onNavigateToMain = { },
        AuthViewModel(AuthRepositoryTest(), UserRepositoryTest(), )
    )
}