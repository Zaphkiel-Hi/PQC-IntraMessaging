package org.niklasunrau.pqcmessenger.presentation.main.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import org.niklasunrau.pqcmessenger.R
import org.niklasunrau.pqcmessenger.domain.util.Route
import org.niklasunrau.pqcmessenger.presentation.composables.CustomNavigationDrawer
import org.niklasunrau.pqcmessenger.presentation.main.viewmodel.MainViewModel

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
        updateRoute = viewModel::updateCurrentRoute,
        onNavigateToRoute = onNavigateToRoute
    ) { innerPadding ->
        Column(
            modifier = Modifier.padding(innerPadding),
        ) {
            Text(text = uiState.currentUser.username)
        }
    }
}