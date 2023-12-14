package org.niklasunrau.pqcmessenger.presentation.util

import androidx.compose.ui.graphics.vector.ImageVector
import org.niklasunrau.pqcmessenger.domain.util.Route

data class NavigationItem(
    val title: String,
    val icon: ImageVector,
    val route: Route
)
