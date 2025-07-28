package org.niklasunrau.pqcmessenger.presentation.auth.screens

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import org.niklasunrau.pqcmessenger.R
import org.niklasunrau.pqcmessenger.theme.MessengerTheme

@Composable
fun ResetPasswordScreen() {
    Text(text = stringResource(id = R.string.reset_password),
        color = MaterialTheme.colorScheme.onSurface)
}

@Preview
@Composable
fun ResetPasswordScreenPreview() {
    MessengerTheme {
        ResetPasswordScreen()
    }
}