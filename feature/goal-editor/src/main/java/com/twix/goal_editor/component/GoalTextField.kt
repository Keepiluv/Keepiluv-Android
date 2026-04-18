package com.twix.goal_editor.component

import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.twix.designsystem.R
import com.twix.designsystem.components.text_field.UnderlineTextField

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun GoalTextField(
    value: String,
    onCommitTitle: (String) -> Unit,
) {
    val focusManager = LocalFocusManager.current
    val density = LocalDensity.current
    var internalValue by rememberSaveable(value) { mutableStateOf(value) }
    var isFocused by remember { mutableStateOf(false) }
    var lastCommitted by remember(value) { mutableStateOf(value.trim()) }

    fun commitIfChanged() {
        val trimmed = internalValue.trim()
        if (trimmed != lastCommitted) {
            lastCommitted = trimmed
            onCommitTitle(trimmed)
        }
    }

    val imeVisibleState =
        remember {
            mutableStateOf(false)
        }

    imeVisibleState.value = WindowInsets.ime.getBottom(density) > 0
    LaunchedEffect(isFocused) {
        var prev = imeVisibleState.value
        snapshotFlow { imeVisibleState.value }
            .collect { now ->
                if (prev && !now && isFocused) {
                    commitIfChanged()
                    focusManager.clearFocus(force = true)
                }
                prev = now
            }
    }

    UnderlineTextField(
        modifier =
            Modifier
                .padding(horizontal = 20.dp)
                .fillMaxWidth()
                .onFocusChanged { state ->
                    isFocused = state.isFocused
                    if (!state.isFocused) commitIfChanged()
                },
        value = internalValue,
        placeHolder = stringResource(R.string.goal_editor_text_field_placeholder),
        onValueChange = { internalValue = it },
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
        keyboardActions =
            KeyboardActions(
                onDone = {
                    commitIfChanged()
                    focusManager.clearFocus(force = true)
                },
            ),
    )
}
