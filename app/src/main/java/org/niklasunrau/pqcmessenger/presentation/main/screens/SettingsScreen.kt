package org.niklasunrau.pqcmessenger.presentation.main.screens

import android.content.res.Configuration
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.SavedStateHandle
import org.niklasunrau.pqcmessenger.R
import org.niklasunrau.pqcmessenger.data.test.AuthRepositoryTest
import org.niklasunrau.pqcmessenger.data.test.ChatRepositoryTest
import org.niklasunrau.pqcmessenger.data.test.DBRepositoryTest
import org.niklasunrau.pqcmessenger.data.test.UserRepositoryTest
import org.niklasunrau.pqcmessenger.domain.util.Route
import org.niklasunrau.pqcmessenger.presentation.composables.CustomNavigationDrawer
import org.niklasunrau.pqcmessenger.presentation.composables.SettingsButton
import org.niklasunrau.pqcmessenger.presentation.main.viewmodel.MainViewModel
import org.niklasunrau.pqcmessenger.theme.MessengerTheme

@Composable
fun SettingsScreen(
    onNavigateToRoute: (Route) -> Unit,
    viewModel: MainViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val scope = rememberCoroutineScope()

    var showDeleteDialog by remember {
        mutableStateOf(false)
    }
    CustomNavigationDrawer(
        title = stringResource(id = R.string.settings),
        navigationItems = viewModel.navigationItemsList,
        currentRoute = uiState.currentRoute,
        updateRoute = viewModel::onCurrentRouteChange,
        onNavigateToRoute = onNavigateToRoute
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
        ) {
            if (showDeleteDialog) {
                AlertDialog(
                    onDismissRequest = {
                        viewModel.onUsernameChange("")
                        showDeleteDialog = false
                    },
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface,

                    title = { Text(text = stringResource(R.string.sure_to_delete))},
                    dismissButton = {
                        Button(
                            onClick = {

                            },
                            modifier = Modifier.heightIn(48.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primaryContainer,
                                contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                            ),
                        ) {
                            Text(text = stringResource(R.string.cancel), style = TextStyle(fontSize = 20.sp))
                        }
                    },
                    confirmButton = {
                        Button(
                            onClick = {

                            },
                            modifier = Modifier.heightIn(48.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.errorContainer,
                                contentColor = MaterialTheme.colorScheme.onErrorContainer
                            ),
                        ) {
                            Text(text = stringResource(R.string.delete), style = TextStyle(fontSize = 20.sp))
                        }
                    }
                )
            }
            SettingsButton(text = stringResource(id = R.string.delete_account), onClicked = {})
        }
    }
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun PreviewSettings() {
    MessengerTheme {
        Surface(
            modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.surface
        ) {
            SettingsScreen(
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