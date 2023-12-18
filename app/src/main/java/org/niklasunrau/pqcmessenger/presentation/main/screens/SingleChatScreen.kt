package org.niklasunrau.pqcmessenger.presentation.main.screens

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import org.niklasunrau.pqcmessenger.presentation.main.viewmodel.MainViewModel

@Composable
fun SingleChatScreen(
    chatId: String,
    onNavigateToChats: () -> Unit,
    viewModel: MainViewModel = hiltViewModel()
) {
    Text(text = chatId)
}