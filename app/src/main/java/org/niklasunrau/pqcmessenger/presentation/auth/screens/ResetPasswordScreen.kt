package org.niklasunrau.pqcmessenger.presentation.auth.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import org.niklasunrau.pqcmessenger.R
import org.niklasunrau.pqcmessenger.theme.MessengerTheme

@Composable
fun ResetPasswordScreen() {
    Surface(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Text(text = stringResource(id = R.string.reset_password))
    }
}

@Preview
@Composable
fun ResetPasswordScreenPreview() {
    MessengerTheme {
        ResetPasswordScreen()
    }
}