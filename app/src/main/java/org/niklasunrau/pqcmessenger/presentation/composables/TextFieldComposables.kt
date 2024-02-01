package org.niklasunrau.pqcmessenger.presentation.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.niklasunrau.pqcmessenger.R
import org.niklasunrau.pqcmessenger.domain.util.Algorithm
import org.niklasunrau.pqcmessenger.presentation.util.Dimens.SmallPadding


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
            cursorColor = MaterialTheme.colorScheme.primary,
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            focusedLabelColor = MaterialTheme.colorScheme.primary,
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
fun ReplyTextField(
    value: String,
    onValueChange: (String) -> Unit,
    onSendClicked: () -> Unit,
    onAlgorithmClicked: (Algorithm.Type) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    var selected by remember { mutableStateOf(Algorithm.Type.MCELIECE) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(SmallPadding),
        verticalAlignment = Alignment.Bottom

    ) {

        Surface(
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.primaryContainer,
            modifier = Modifier
                .weight(1f)
        ) {
            Row(
                modifier = Modifier.padding(2.dp),
                verticalAlignment = Alignment.Bottom
            ) {
                Box(
                    modifier = Modifier
                        .size(45.dp)
                        .clickable(
                            onClick = { expanded = true },
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.Lock,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier
                            .padding(SmallPadding)
                    )
                }
                DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                    Algorithm.Type.entries.forEach { type ->
                        DropdownMenuItem(
                            text = { Text(Algorithm.name[type]!!) },
                            leadingIcon = { if (type == selected) Icon(Icons.Filled.Check, null) },
                            onClick = {
                                selected = type
                                expanded = false
                                onAlgorithmClicked(type)
                            })
                    }
                }
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .heightIn(45.dp),
                    contentAlignment = Alignment.CenterStart
                ) {
                    BasicTextField(
                        value = value,
                        onValueChange = onValueChange,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp, horizontal = 8.dp),
                        textStyle = TextStyle.Default.copy(color = MaterialTheme.colorScheme.onPrimaryContainer, fontSize = 20.sp),
                        cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                        decorationBox = { innerTextField ->
                            if (value.isBlank()) {
                                Text(stringResource(R.string.message), color = MaterialTheme.colorScheme.onPrimaryContainer, fontSize = 20.sp)
                            }
                            innerTextField()
                        }
//
                    )
                }
            }
        }
        Spacer(modifier = Modifier.width(10.dp))
        IconButton(
            onClick = onSendClicked,
            modifier = Modifier
                .size(48.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.Send,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.primary)
                    .padding(SmallPadding)
            )
        }
    }
}

