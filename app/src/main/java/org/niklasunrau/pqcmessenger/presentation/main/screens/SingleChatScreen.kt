package org.niklasunrau.pqcmessenger.presentation.main.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import org.niklasunrau.pqcmessenger.data.test.AuthRepositoryTest
import org.niklasunrau.pqcmessenger.data.test.ChatRepositoryTest
import org.niklasunrau.pqcmessenger.data.test.UserRepositoryTest
import org.niklasunrau.pqcmessenger.presentation.composables.CustomScaffold
import org.niklasunrau.pqcmessenger.presentation.composables.ReplyTextField
import org.niklasunrau.pqcmessenger.presentation.main.viewmodel.MainViewModel

@Composable
fun SingleChatScreen(
    chatId: String,
    onNavigateToChats: () -> Unit,
    viewModel: MainViewModel = hiltViewModel()
) {

    CustomScaffold(
        title = { Text(text = chatId) },
        navigationIcon = {
            IconButton(onClick = { onNavigateToChats() }) {
                Icon(Icons.Filled.ArrowBack, null)
            }
        },
        actions = { },
        floatingActionButton = { }) {
        Column(
            Modifier.fillMaxSize()
        ) {
            Spacer(modifier = Modifier.weight(1f))
            ReplyTextField(value = "", onValueChange = {}, onSendClicked = {})
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
            chatId = "",
            onNavigateToChats = { },
            MainViewModel(AuthRepositoryTest(), UserRepositoryTest(), ChatRepositoryTest())
        )
    }
}