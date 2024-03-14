package org.niklasunrau.pqcmessenger.presentation.util

import androidx.compose.ui.graphics.vector.ImageVector

data class NavigationItem(
    val titleId: Int,
    val icon: ImageVector,
    val screen: Screen,
)
