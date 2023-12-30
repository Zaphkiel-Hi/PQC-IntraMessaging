package org.niklasunrau.pqcmessenger.presentation.auth.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import org.niklasunrau.pqcmessenger.R
import org.niklasunrau.pqcmessenger.presentation.auth.viewmodel.AuthViewModel
import org.niklasunrau.pqcmessenger.presentation.composables.AutoSizeText
import org.niklasunrau.pqcmessenger.presentation.composables.CustomFilledButton
import org.niklasunrau.pqcmessenger.presentation.composables.CustomOutlinedButton
import org.niklasunrau.pqcmessenger.presentation.util.Dimens.LargePadding
import org.niklasunrau.pqcmessenger.presentation.util.Dimens.MediumPadding
import org.niklasunrau.pqcmessenger.presentation.util.Dimens.SmallPadding

@Composable
fun StartScreen(
    onNavigateToLogIn: () -> Unit,
    onNavigateToSignUp: () -> Unit,
    viewModel: AuthViewModel = hiltViewModel()
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(MediumPadding),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(fraction = 0.4f),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_icon),
                contentDescription = null,
                Modifier.fillMaxSize(fraction = 0.5f)
            )
        }
        Spacer(modifier = Modifier.height(MediumPadding))
        Text(
            text = stringResource(id = R.string.hello),
            style = MaterialTheme.typography.displayLarge
        )
        AutoSizeText(
            text = stringResource(id = R.string.welcome),
            style = MaterialTheme.typography.headlineSmall
        )
        Spacer(modifier = Modifier.height(LargePadding))

        CustomFilledButton(
            text = stringResource(id = R.string.login),
            onClicked = onNavigateToLogIn,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = SmallPadding)
        )

        Spacer(modifier = Modifier.height(MediumPadding))

        CustomOutlinedButton(
            text = stringResource(id = R.string.signup),
            onClicked = onNavigateToSignUp,
            modifier = Modifier.padding(horizontal = SmallPadding)
        )
        CustomOutlinedButton(
            text = "Generate",
            onClicked = { viewModel.generate() },
            modifier = Modifier.padding(horizontal = SmallPadding)
        )
    }
}