package com.twix.onboarding.invite.component

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
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
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
        value = inviteCode,
        onValueChange = { newText ->
            val filtered = newText.filterNot { it.isWhitespace() }
            if (filtered.length <= InviteCode.INVITE_CODE_LENGTH && filtered != inviteCode) {
                onValueChange(filtered)
            }
        },
        decorationBox = {
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                repeat(InviteCode.INVITE_CODE_LENGTH) { index ->
                    CodeBox(index, inviteCode)
                }
            }
        },
        keyboardOptions =
            KeyboardOptions(
                capitalization = KeyboardCapitalization.Characters,
                imeAction = ImeAction.None,
            ),
    )
}

@Composable
private fun CodeBox(
    index: Int,
    code: String,
) {
    val isFocused = code.length == index

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

            isFocused -> Cursor()
        }
    }
}

@Composable
private fun Cursor() {
    Box(
        modifier =
            Modifier
                .width(2.dp)
                .height(24.dp)
                .background(GrayColor.C500),
    )
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
