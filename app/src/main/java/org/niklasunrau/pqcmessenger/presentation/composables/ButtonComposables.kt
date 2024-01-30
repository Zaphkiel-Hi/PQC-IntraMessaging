package org.niklasunrau.pqcmessenger.presentation.composables

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun CustomFilledButton(
    text: String, modifier: Modifier = Modifier, onClicked: () -> Unit
) {
    Button(
        onClick = {
            onClicked()
        },
        modifier = modifier.heightIn(48.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary
        ),
    ) {
        Text(text = text, style = TextStyle(fontSize = 20.sp))
    }
}

@Composable
fun CustomOutlinedButton(
    text: String, modifier: Modifier = Modifier, onClicked: () -> Unit
) {
    Button(
        onClick = {
            onClicked()
        },
        modifier = modifier
            .fillMaxWidth()
            .heightIn(48.dp),
        border = BorderStroke(width = 2.dp, MaterialTheme.colorScheme.primary),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Transparent, contentColor = MaterialTheme.colorScheme.primary
        ),
    ) {
        Text(text = text, style = TextStyle(fontSize = 20.sp))
    }
}

