package com.twix.designsystem.components.comment

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.twix.designsystem.R
import com.twix.designsystem.components.comment.model.CommentUiModel
import com.twix.designsystem.theme.GrayColor
import com.twix.designsystem.theme.TwixTheme
import com.twix.ui.extension.noRippleClickable
import com.twix.ui.keyboard.Keyboard
import com.twix.ui.keyboard.keyboardAsState

val CIRCLE_PADDING_START: Dp = 50.dp
val CIRCLE_SIZE: Dp = 64.dp
private val CIRCLE_GAP: Dp = CIRCLE_PADDING_START - CIRCLE_SIZE

@Composable
fun CommentTextField(
    uiModel: CommentUiModel,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    onCommitComment: (String) -> Unit = {},
    onFocusChanged: (Boolean) -> Unit = {},
    onPositioned: (Rect) -> Unit = {},
) {
    val focusManager = LocalFocusManager.current
    val focusRequester = remember { FocusRequester() }
    val keyboardState by keyboardAsState()

    var internalValue by rememberSaveable(uiModel.comment) { mutableStateOf(uiModel.comment) }
    var isInitialized by remember { mutableStateOf(false) }

    LaunchedEffect(keyboardState) {
        if (!isInitialized) {
            isInitialized = true
        } else {
            when (keyboardState) {
                Keyboard.Opened -> Unit
                Keyboard.Closed -> {
                    onCommitComment(internalValue.trim())
                    focusManager.clearFocus()
                }
            }
        }
    }

    LaunchedEffect(uiModel.isFocused) {
        if (uiModel.isFocused) focusRequester.requestFocus()
    }

    Box(
        modifier =
            modifier
                .onGloballyPositioned { coordinates ->
                    onPositioned(coordinates.boundsInRoot())
                }.noRippleClickable {
                    focusRequester.requestFocus()
                },
    ) {
        TextField(
            value = internalValue,
            onValueChange = { newValue ->
                if (newValue.length <= CommentUiModel.COMMENT_COUNT) {
                    internalValue = newValue
                }
            },
            enabled = enabled,
            modifier =
                Modifier
                    .width(0.dp)
                    .alpha(0f)
                    .focusRequester(focusRequester)
                    .onFocusChanged { state ->
                        onFocusChanged(state.isFocused)
                    },
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
            singleLine = true,
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(CIRCLE_GAP),
            modifier =
                Modifier.drawWithCache {
                    val radius = size.height / 2
                    val paddingStart = CIRCLE_PADDING_START.toPx()

                    onDrawBehind {
                        repeat(CommentUiModel.COMMENT_COUNT) { index ->
                            val cx = radius + index * paddingStart

                            drawCircle(
                                color = GrayColor.C500,
                                radius = radius,
                                center = Offset(cx, radius),
                                style = Stroke(2.dp.toPx()),
                            )
                        }
                    }
                },
        ) {
            repeat(CommentUiModel.COMMENT_COUNT) { index ->
                val char =
                    if (uiModel.isFocused || internalValue.isNotEmpty()) {
                        internalValue.getOrNull(index)?.toString()
                    } else {
                        stringResource(R.string.comment_text_field_placeholder)[index].toString()
                    }.orEmpty()

                CommentCircle(
                    text = char,
                    showPlaceholder = !uiModel.isFocused && internalValue.isEmpty(),
                    showCursor = uiModel.isFocused && index == internalValue.length,
                    modifier =
                        Modifier.noRippleClickable {
                            focusRequester.requestFocus()
                        },
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun CommentTextFieldPreview() {
    TwixTheme {
        var text by remember { mutableStateOf("") }
        var isFocused by remember { mutableStateOf(false) }
        CommentTextField(
            uiModel = CommentUiModel(text, isFocused),
            onFocusChanged = { isFocused = it },
            onPositioned = {},
        )
    }
}
