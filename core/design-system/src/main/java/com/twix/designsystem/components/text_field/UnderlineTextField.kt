package com.twix.designsystem.components.text_field

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.twix.designsystem.components.text.AppText
import com.twix.designsystem.theme.GrayColor
import com.twix.designsystem.theme.LocalAppTypography
import com.twix.designsystem.theme.toTextStyle
import com.twix.domain.model.enums.AppTextStyle

@Composable
fun UnderlineTextField(
    modifier: Modifier = Modifier,
    value: String,
    placeHolder: String = "",
    textStyle: AppTextStyle = AppTextStyle.T2,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    singleLine: Boolean = true,
    showTrailing: Boolean = false,
    maxLines: Int = 1,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    trailing: (@Composable () -> Unit)? = null,
    onValueChange: (String) -> Unit,
) {
    val typo = LocalAppTypography.current

    Column(
        modifier =
            Modifier
                .height(52.dp),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.Center,
    ) {
        Spacer(Modifier.height(4.dp))

        Box(
            modifier =
                modifier
                    .padding(horizontal = 8.dp, vertical = 10.dp),
            contentAlignment = Alignment.CenterStart,
        ) {
            val shouldShowTrailing = trailing != null && showTrailing && value.isNotBlank()

            if (value.isBlank()) {
                AppText(
                    text = placeHolder,
                    style = textStyle,
                    color = GrayColor.C200,
                    modifier =
                        Modifier
                            .align(Alignment.CenterStart),
                )
            }

            Row(
                modifier =
                    Modifier
                        .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                BasicTextField(
                    value = value,
                    textStyle = textStyle.toTextStyle(typo).copy(color = GrayColor.C500),
                    onValueChange = onValueChange,
                    enabled = enabled,
                    readOnly = readOnly,
                    singleLine = singleLine,
                    maxLines = maxLines,
                    keyboardOptions = keyboardOptions,
                    keyboardActions = keyboardActions,
                    modifier = Modifier.weight(1f),
                )

                if (shouldShowTrailing) {
                    Box(
                        modifier =
                            Modifier
                                .padding(start = 10.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        trailing.invoke()
                    }
                }
            }
        }

        Spacer(Modifier.height(4.dp))

        HorizontalDivider(thickness = 1.dp, color = GrayColor.C500, modifier = modifier)
    }
}
