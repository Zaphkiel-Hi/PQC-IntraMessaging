package org.niklasunrau.pqcmessenger.presentation.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import org.niklasunrau.pqcmessenger.theme.PrimaryColor

@Composable
fun CustomCircularProgress(
    isDisplayed: Boolean
) {
    if (isDisplayed) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .alpha(0.5f)
                .background(Color.LightGray)
                .clickable(enabled = false) {},
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(
                color = PrimaryColor
            )
        }
    }
}

