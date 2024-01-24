package org.niklasunrau.pqcmessenger.presentation.main.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.SavedStateHandle
import coil.compose.AsyncImage
import org.niklasunrau.pqcmessenger.data.test.AuthRepositoryTest
import org.niklasunrau.pqcmessenger.data.test.ChatRepositoryTest
import org.niklasunrau.pqcmessenger.data.test.DBRepositoryTest
import org.niklasunrau.pqcmessenger.data.test.UserRepositoryTest
import org.niklasunrau.pqcmessenger.presentation.composables.CustomScaffold
import org.niklasunrau.pqcmessenger.presentation.composables.ReplyTextField
import org.niklasunrau.pqcmessenger.presentation.main.viewmodel.MainViewModel
import org.niklasunrau.pqcmessenger.presentation.util.Dimens.LargePadding
import org.niklasunrau.pqcmessenger.presentation.util.Dimens.SmallPadding

@Composable
fun SingleChatScreen(
    chatId: String, onNavigateToChats: () -> Unit, viewModel: MainViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val chatListState = rememberLazyListState()




    LaunchedEffect(key1 = Unit) {
        viewModel.initializeChat(chatId)
    }
    LaunchedEffect(key1 = uiState.currentChatMessages) {
        chatListState.animateScrollToItem(chatListState.layoutInfo.totalItemsCount)
    }
    BackHandler {
        viewModel.closeChat()
        onNavigateToChats()
    }

    CustomScaffold(title = {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = uiState.idToChat[chatId]!!.icon,
                contentDescription = null,
                modifier = Modifier
                    .padding(SmallPadding)
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
            )
            Text(
                text = uiState.idToChat[chatId]!!.name,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
    }, navigationIcon = {
        IconButton(onClick = {
            viewModel.closeChat()
            onNavigateToChats()
        }) {
            Icon(
                Icons.Filled.ArrowBack, null,
                tint = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
    }, actions = { }, floatingActionButton = { }) { innerPadding ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(innerPadding), verticalArrangement = Arrangement.SpaceBetween
        ) {
            LazyColumn(
                modifier = Modifier.weight(1f), state = chatListState
            ) {
                var prevAuthor: String? = null
                items(uiState.currentChatMessages) { message ->
                    var alignment = Alignment.CenterStart
                    var backgroundColor = MaterialTheme.colorScheme.secondaryContainer
                    var textColor = MaterialTheme.colorScheme.onSecondaryContainer
                    var topPadding = 8.dp
                    var startPadding = SmallPadding
                    var endPadding = LargePadding
                    if (message.fromId == uiState.loggedInUser.id) {
                        alignment = Alignment.CenterEnd
                        backgroundColor = MaterialTheme.colorScheme.primaryContainer
                        textColor = MaterialTheme.colorScheme.onPrimaryContainer
                        startPadding = LargePadding
                        endPadding = SmallPadding
                    }
                    if (prevAuthor == null) {
                        prevAuthor = message.fromId
                    } else {
                        topPadding = if (prevAuthor == message.fromId) 2.dp else 8.dp
                        prevAuthor = message.fromId
                    }
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = startPadding, end = endPadding, top = topPadding),
                        contentAlignment = alignment
                    ) {
                        Text(
                            text = message.text,
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(backgroundColor)
                                .padding(8.dp),
                            color = textColor,
                        )
                    }

                }
            }
            ReplyTextField(value = uiState.currentText,
                onValueChange = { viewModel.onCurrentTextChange(it) },
                onSendClicked = { viewModel.onSendSingleMessage(chatId, uiState.currentText) },
                onAlgorithmClicked = { alg -> viewModel.onCurrentAlgChange(alg) })
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SingleChatPreview() {
    Surface(
        modifier = Modifier.fillMaxSize()
    ) {
        SingleChatScreen(
            chatId = "", onNavigateToChats = { }, MainViewModel(
                AuthRepositoryTest(),
                UserRepositoryTest(),
                ChatRepositoryTest(),
                DBRepositoryTest(),
                savedStateHandle = SavedStateHandle(
                )
            )
        )
    }
}