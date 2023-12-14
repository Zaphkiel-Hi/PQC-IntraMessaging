package org.niklasunrau.pqcmessenger.presentation.composables

import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import org.niklasunrau.pqcmessenger.theme.AccentColor


@Composable
fun AutoSizeText(
    text: String,
    style: TextStyle,
    modifier: Modifier = Modifier
) {
    var scaledTextStyle by remember { mutableStateOf(style) }
    var readyToDraw by remember { mutableStateOf(false) }

    Text(
        text,
        modifier.drawWithContent {
            if (readyToDraw) {
                drawContent()
            }
        },
        style = scaledTextStyle,
        softWrap = false,
        onTextLayout = { textLayoutResult ->
            if (textLayoutResult.didOverflowWidth) {
                scaledTextStyle =
                    scaledTextStyle.copy(fontSize = scaledTextStyle.fontSize * 0.9)
            } else {
                readyToDraw = true
            }
        }
    )
}


@Composable
fun CustomClickableText(
    textBlocks: List<String>,
    onClicks: List<() -> Unit>,
    modifier: Modifier = Modifier,
    firstClickableIsSecond: Boolean = true
) {
    val annotatedString = buildAnnotatedString {
        for ((index, block) in textBlocks.withIndex()) {
            if (index % 2 == if (firstClickableIsSecond) 1 else 0) {
                pushStringAnnotation(tag = block, annotation = "")
                withStyle(SpanStyle(color = AccentColor)) {
                    append(block)
                }
                pop()
            } else {
                append(block)
            }
        }
    }


    ClickableText(
        text = annotatedString,
        style = TextStyle.Default.copy(color = LocalContentColor.current),
        modifier = modifier,
        onClick = { offset ->
            for ((index, block) in textBlocks.withIndex()) if (index % 2 == if (firstClickableIsSecond) 1 else 0) {
                annotatedString.getStringAnnotations(tag = block, start = offset, end = offset).firstOrNull()?.let {
                    onClicks[(index / 2)]()
                }
            }
        }
    )
}