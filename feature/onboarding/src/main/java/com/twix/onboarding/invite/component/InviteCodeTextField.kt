package com.twix.onboarding.invite.component

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.twix.designsystem.components.text.AppText
import com.twix.designsystem.theme.GrayColor
import com.twix.designsystem.theme.TwixTheme
import com.twix.domain.model.enums.AppTextStyle
import com.twix.domain.model.invitecode.InviteCode

@Composable
fun InviteCodeTextField(
    inviteCode: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    BasicTextField(
        modifier = modifier,
        value = TextFieldValue(inviteCode, selection = TextRange(inviteCode.length)),
        onValueChange = { newValue ->
            val filteredText = newValue.text.filterNot { char -> char.isWhitespace() }
            if (filteredText.length <= InviteCode.INVITE_CODE_LENGTH) {
                onValueChange(filteredText)
            }
        },
        decorationBox = {
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.29.dp),
            ) {
                repeat(InviteCode.INVITE_CODE_LENGTH) { index ->
                    CodeBox(index, inviteCode)
                }
            }
        },
        keyboardOptions =
            KeyboardOptions(
                capitalization = KeyboardCapitalization.Characters,
            ),
    )
}

@Composable
private fun CodeBox(
    index: Int,
    code: String,
) {
    val isFocused = code.length == index
    val infiniteTransition = rememberInfiniteTransition()
    val alpha by infiniteTransition.animateFloat(
        0f,
        1f,
        infiniteRepeatable(tween(500), RepeatMode.Reverse),
    )

    Box(
        modifier =
            Modifier
                .width(36.dp)
                .height(58.dp)
                .border(
                    1.dp,
                    when {
                        isFocused -> GrayColor.C500
                        else -> GrayColor.C200
                    },
                    RoundedCornerShape(8.dp),
                ),
        contentAlignment = Alignment.Center,
    ) {
        when {
            index < code.length -> {
                AppText(
                    text = code[index].toString(),
                    style = AppTextStyle.H3,
                    color = GrayColor.C500,
                )
            }

            isFocused -> {
                Box(
                    modifier =
                        Modifier
                            .width(2.dp)
                            .height(24.dp)
                            .background(GrayColor.C500.copy(alpha = alpha)),
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun InviteCodeTextFieldPreview() {
    TwixTheme {
        InviteCodeTextField(
            inviteCode = "12345678",
            onValueChange = { },
        )
    }
}
