package org.niklasunrau.pqcmessenger.presentation.main.screens

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.SavedStateHandle
import coil.compose.AsyncImage
import org.niklasunrau.pqcmessenger.R
import org.niklasunrau.pqcmessenger.data.test.AuthRepositoryTest
import org.niklasunrau.pqcmessenger.data.test.ChatRepositoryTest
import org.niklasunrau.pqcmessenger.data.test.DBRepositoryTest
import org.niklasunrau.pqcmessenger.data.test.UserRepositoryTest
import org.niklasunrau.pqcmessenger.domain.util.Route
import org.niklasunrau.pqcmessenger.presentation.composables.CustomNavigationDrawer
import org.niklasunrau.pqcmessenger.presentation.main.viewmodel.MainViewModel
import org.niklasunrau.pqcmessenger.presentation.util.Dimens.MediumPadding
import org.niklasunrau.pqcmessenger.presentation.util.Dimens.SmallPadding
import org.niklasunrau.pqcmessenger.theme.MessengerTheme

@Composable
fun ProfileScreen(
    onNavigateToRoute: (Route) -> Unit,
    viewModel: MainViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    CustomNavigationDrawer(
        title = stringResource(id = R.string.profile),
        navigationItems = viewModel.navigationItemsList,
        currentRoute = uiState.currentRoute,
        updateRoute = viewModel::onCurrentRouteChange,
        onNavigateToRoute = onNavigateToRoute
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(all = MediumPadding)
                .fillMaxSize(),
        ) {
            Spacer(modifier = Modifier.height(MediumPadding))
            AsyncImage(
                model = uiState.loggedInUser.image,
                contentDescription = null,
                modifier = Modifier
                    .clip(CircleShape)
                    .aspectRatio(1f)
                    .weight(0.3f)
                    .align(Alignment.CenterHorizontally)
                    .clickable {
                        viewModel.selectImage()
                    }
                    .background(
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
            )
            Spacer(modifier = Modifier.height(SmallPadding))
            Text(
                text = if (uiState.loggedInUser.username != "") uiState.loggedInUser.username else "Niklas",
                modifier = Modifier.align(
                    Alignment.CenterHorizontally
                ),
                style = MaterialTheme.typography.displaySmall,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.weight(0.7f))
        }
    }

}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun PreviewProfile() {
    MessengerTheme {
        Surface(
            modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.surface
        ) {
            ProfileScreen(
                {},
                MainViewModel(
                    AuthRepositoryTest(),
                    UserRepositoryTest(),
                    ChatRepositoryTest(),
                    DBRepositoryTest(),
                    SavedStateHandle()
                )
            )
        }
    }
}