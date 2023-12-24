package org.niklasunrau.pqcmessenger.presentation.composables

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import org.niklasunrau.pqcmessenger.R
import org.niklasunrau.pqcmessenger.presentation.util.Dimens.SmallPadding
import org.niklasunrau.pqcmessenger.theme.AccentColor
import org.niklasunrau.pqcmessenger.theme.SecondaryBackgroundColor


@Composable
fun LogInTextField(
    label: String,
    icon: ImageVector,
    value: String = "",
    type: KeyboardType = KeyboardType.Text,
    isLast: Boolean = false,
    errorText: String = "",
    onValueChanged: (String) -> Unit
) {
    val text = remember {
        mutableStateOf(value)
    }
    val visible = remember {
        mutableStateOf(type != KeyboardType.Password)
    }
    val focusManager = LocalFocusManager.current
    OutlinedTextField(
        label = { Text(text = label) },
        value = text.value,
        colors = OutlinedTextFieldDefaults.colors(
            cursorColor = AccentColor,
            focusedBorderColor = AccentColor,
            focusedLabelColor = AccentColor,
        ),
        modifier = Modifier
            .fillMaxWidth(),
        maxLines = 1,
        singleLine = true,
        isError = errorText.isNotEmpty(),
        supportingText = {
            if (errorText.isNotEmpty()) {
                Text(
                    text = errorText,
                    modifier = Modifier.fillMaxWidth(),
                    color = MaterialTheme.colorScheme.error
                )
            }
        },

        keyboardOptions = KeyboardOptions(
            keyboardType = type, imeAction = if (!isLast) ImeAction.Next else ImeAction.Done
        ),
        keyboardActions = if (isLast) KeyboardActions {
            focusManager.clearFocus()
        } else KeyboardActions.Default,
        onValueChange = {
            text.value = it
            onValueChanged(it)
        },
        leadingIcon = {
            Icon(icon, contentDescription = null)
        },
        trailingIcon = {
            if (type == KeyboardType.Password) {
                val iconVisible = if (visible.value) {
                    Icons.Filled.Visibility
                } else {
                    Icons.Filled.VisibilityOff
                }
                IconButton(onClick = { visible.value = !visible.value }) {
                    Icon(iconVisible, contentDescription = null)
                }
            }
        },
        visualTransformation = if (visible.value) VisualTransformation.None else PasswordVisualTransformation()
    )
}

@Composable
fun ReplyTextField(value: String, onValueChange: (String) -> Unit, onSendClicked: () -> Unit){
    Column(
        modifier = Modifier.fillMaxWidth().padding(top = SmallPadding)
    ) {
        Divider()
        Row(
           modifier = Modifier
               .fillMaxWidth()
               .padding(SmallPadding),
            verticalAlignment = Alignment.CenterVertically

        ){
            OutlinedTextField(
                modifier = Modifier.weight(1f),
                value = value,
                onValueChange = onValueChange,
                shape = CircleShape,
                colors = OutlinedTextFieldDefaults.colors(
                    cursorColor = AccentColor,
                    focusedContainerColor = SecondaryBackgroundColor,
                    unfocusedContainerColor = SecondaryBackgroundColor,
                    focusedBorderColor = Color.Transparent,
                    unfocusedBorderColor = Color.Transparent
                ),
            )
            Spacer(Modifier.width(SmallPadding))
            CustomFilledButton(text = stringResource(R.string.send), onClicked = onSendClicked)
        }
    }
}
