package org.niklasunrau.pqcmessenger.presentation.main.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import org.niklasunrau.pqcmessenger.presentation.composables.CustomDrawerScaffold
import org.niklasunrau.pqcmessenger.presentation.main.viewmodel.MainViewModel
import org.niklasunrau.pqcmessenger.presentation.util.Either

@Composable
fun ContactScreen(
    drawerState: DrawerState,
    title: String,
    viewModel: MainViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    CustomDrawerScaffold(
        drawerState = drawerState,
        title = Either.Left(title),
    ) { innerPadding ->
        Column(
            modifier = Modifier.padding(innerPadding),
        ) {
        }
    }
}