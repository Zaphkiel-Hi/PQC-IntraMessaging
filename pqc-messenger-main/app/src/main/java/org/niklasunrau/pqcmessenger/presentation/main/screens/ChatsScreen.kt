package org.niklasunrau.pqcmessenger.presentation.main.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.automirrored.filled.Message
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DrawerState
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.niklasunrau.pqcmessenger.R
import org.niklasunrau.pqcmessenger.domain.util.ChatType
import org.niklasunrau.pqcmessenger.presentation.composables.CustomCircularProgress
import org.niklasunrau.pqcmessenger.presentation.composables.CustomDrawerScaffold
import org.niklasunrau.pqcmessenger.presentation.composables.CustomFilledButton
import org.niklasunrau.pqcmessenger.presentation.main.viewmodel.MainViewModel
import org.niklasunrau.pqcmessenger.presentation.util.Dimens.SmallPadding
import org.niklasunrau.pqcmessenger.presentation.util.Either

@Composable
fun ChatsScreen(
    drawerState: DrawerState,
    title: String,
    onNavigateToAuth: () -> Unit,
    onNavigateToSingleChat: (String) -> Unit,
    viewModel: MainViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val scope = rememberCoroutineScope()

    var showAddDialog by remember {
        mutableStateOf(false)
    }
    CustomDrawerScaffold(
        drawerState = drawerState,
        title = Either.Left(title),
        actions = {
            IconButton(onClick = {
                viewModel.signOut()
                onNavigateToAuth()
            }) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Logout,
                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                    contentDescription = null
                )
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    showAddDialog = true
                },
                shape = CircleShape,
                containerColor = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(bottom = SmallPadding, end = SmallPadding)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Message,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }
        }
    ) { paddingValues ->
        if (showAddDialog) {
            AlertDialog(
                onDismissRequest = {
                    viewModel.onUsernameChange("")
                    showAddDialog = false
                },
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,

                title = { Text(text = stringResource(R.string.start_new_chat)) },
                text = {
                    OutlinedTextField(
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedBorderColor = MaterialTheme.colorScheme.onPrimaryContainer,
                            cursorColor = MaterialTheme.colorScheme.onPrimaryContainer,
                            focusedBorderColor = MaterialTheme.colorScheme.onPrimaryContainer,
                            focusedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer,
                            focusedTextColor = MaterialTheme.colorScheme.onPrimaryContainer
                        ),
                        label = { Text(text = stringResource(id = R.string.username)) },
                        maxLines = 1,
                        value = uiState.newChatUsername,
                        onValueChange = {
                            viewModel.onUsernameChange(it)
                        },
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                        isError = uiState.newChatError.asString().isNotEmpty(),
                        supportingText = {
                            if (uiState.newChatError.asString().isNotEmpty()) {
                                Text(
                                    text = uiState.newChatError.asString(),
                                    modifier = Modifier.fillMaxWidth(),
                                    color = MaterialTheme.colorScheme.error
                                )
                            }
                        }
                    )
                },
                confirmButton = {
                    CustomFilledButton(
                        text = stringResource(R.string.start_chat),
                        onClicked = {
                            scope.launch {
                                viewModel.startNewSingleChat(uiState.newChatUsername)
                                    .collectLatest { successful ->
                                        if (successful) {
                                            showAddDialog = false
                                            viewModel.onUsernameChange("")
                                        }
                                    }
                            }
                        }
                    )
                }
            )
        }

        if (uiState.idToChat.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "No chats", style = MaterialTheme.typography.titleLarge)
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                items(uiState.idToChat.values.toList()) { chat ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(75.dp)
                            .clickable {
                                onNavigateToSingleChat(chat.id)
                            },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (chat.type == ChatType.SINGLE) {
                            val otherUserId = viewModel.getOtherUserId(chat)
                            val otherUser = uiState.idToUser[otherUserId]
                            otherUser?.let {
                                AsyncImage(
                                    model = it.image,
                                    contentDescription = null,
                                    modifier = Modifier
                                        .padding(SmallPadding)
                                        .size(50.dp)
                                        .clip(CircleShape)
                                        .background(MaterialTheme.colorScheme.onSurface)
                                )
                                Text(
                                    text = otherUser.username,
                                    style = MaterialTheme.typography.titleLarge,
                                    color = MaterialTheme.colorScheme.onSurface
                                )

                            }
                        } else {
                            TODO("groups chats")
                        }
                    }
                    HorizontalDivider()
                }
            }
        }

    }
    CustomCircularProgress(isDisplayed = uiState.isLoading, text = "Loading secret keys...")
}