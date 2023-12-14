package org.niklasunrau.pqcmessenger.presentation.main.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import org.niklasunrau.pqcmessenger.R
import org.niklasunrau.pqcmessenger.data.test.AuthRepositoryTest
import org.niklasunrau.pqcmessenger.data.test.UserRepositoryTest
import org.niklasunrau.pqcmessenger.domain.util.Route
import org.niklasunrau.pqcmessenger.presentation.composables.CustomNavigationDrawer
import org.niklasunrau.pqcmessenger.presentation.main.viewmodel.MainViewModel

@Composable
fun HomeScreen(
    onNavigateToRoute: (Route) -> Unit,
    onNavigateToAuth: () -> Unit,
    viewModel: MainViewModel = hiltViewModel()
) {

    val uiState by viewModel.uiState.collectAsState()
    CustomNavigationDrawer(
        title = stringResource(id = R.string.app_name),
        navigationItems = viewModel.navigationItemsList,
        currentRoute = uiState.currentRoute,
        updateRoute = viewModel::updateCurrentRoute,
        onNavigateToRoute = onNavigateToRoute
    ) { innerPadding ->
        Column(
            modifier = Modifier.padding(innerPadding),
        ) {
            IconButton(onClick = {
                viewModel.singOut(onNavigateToAuth)
            }) {
                Icon(
                    imageVector = Icons.Filled.Logout,
                    contentDescription = null
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun Preview() {
    HomeScreen(
        { }, { },
        MainViewModel(AuthRepositoryTest(), UserRepositoryTest())
    )
}