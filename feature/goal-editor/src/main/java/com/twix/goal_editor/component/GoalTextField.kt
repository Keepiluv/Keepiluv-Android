package com.twix.goal_editor.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.twix.designsystem.R
import com.twix.designsystem.components.text.AppText
import com.twix.designsystem.components.text_field.UnderlineTextField
import com.twix.designsystem.theme.SystemColor
import com.twix.domain.model.enums.AppTextStyle
import com.twix.ui.extension.noRippleClickable

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

    Column(
        modifier =
            Modifier
                .height(96.dp),
        verticalArrangement = Arrangement.Top,
    ) {
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
            maxLength = 14,
            showTrailing = internalValue.isNotBlank(),
            onValueChange = { internalValue = it },
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            keyboardActions =
                KeyboardActions(
                    onDone = {
                        commitIfChanged()
                        focusManager.clearFocus(force = true)
                    },
                ),
            trailing = {
                Image(
                    painter = painterResource(R.drawable.ic_clear_text),
                    contentDescription = null,
                    modifier = Modifier.noRippleClickable { internalValue = "" },
                )
            },
        )

        if (internalValue.length in 2..14) {
            Row(
                modifier =
                    Modifier
                        .padding(top = 8.dp, start = 20.dp)
                        .height(17.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                Image(
                    painter = painterResource(R.drawable.ic_check_success),
                    contentDescription = null,
                    modifier =
                        Modifier
                            .size(16.dp),
                )

                AppText(
                    text = stringResource(R.string.goal_editor_text_filed_guide),
                    style = AppTextStyle.C2,
                    color = SystemColor.Success,
                )
            }
        }
    }
}
