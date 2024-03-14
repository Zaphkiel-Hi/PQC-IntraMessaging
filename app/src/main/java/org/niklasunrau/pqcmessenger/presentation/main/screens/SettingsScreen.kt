package org.niklasunrau.pqcmessenger.presentation.main.screens

import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.res.Configuration
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
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
import org.niklasunrau.pqcmessenger.presentation.composables.CustomDrawerScaffold
import org.niklasunrau.pqcmessenger.presentation.main.viewmodel.MainViewModel
import org.niklasunrau.pqcmessenger.presentation.util.Either
import org.niklasunrau.pqcmessenger.theme.MessengerTheme


@Composable
fun SettingsScreen(
    drawerState: DrawerState,
    title: String,
    viewModel: MainViewModel = hiltViewModel()
) {
    val context = LocalContext.current

    var showDeleteDialog by remember {
        mutableStateOf(false)
    }
    CustomDrawerScaffold(
        drawerState = drawerState,
        title = Either.Left(title),
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
                                showDeleteDialog = false
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
                                val mIntent = Intent(Intent.ACTION_SEND)
                                mIntent.setDataAndType(Uri.parse("mailto:"),"text/plain")
                                mIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf("unrau73.wu@gmail.com"))
                                mIntent.putExtra(Intent.EXTRA_SUBJECT, "deletion request")
                                mIntent.putExtra(Intent.EXTRA_TEXT, "Username:\nPassword:")
                                try{
                                    context.startActivity(mIntent)
                                }catch(ex: ActivityNotFoundException) {
                                    Toast.makeText(
                                        context,
                                        context.getString(R.string.no_app_available_to_send_email),
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }

                                showDeleteDialog = false
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
//            SettingsButton(text = stringResource(id = R.string.delete_account), onClicked = {showDeleteDialog = true})
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
                drawerState = DrawerState(initialValue = DrawerValue.Closed),
                title = "Settings",
                viewModel = MainViewModel(
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