package com.twix.settings.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.twix.designsystem.R
import com.twix.designsystem.components.text.AppText
import com.twix.designsystem.components.text_field.ValidateUnderlineTextField
import com.twix.designsystem.theme.GrayColor
import com.twix.domain.model.enums.AppTextStyle
import com.twix.ui.extension.noRippleClickable

@Composable
fun ProfileInfo(
    nickname: String,
    isEditMode: Boolean,
    onCommitNickName: (String) -> Unit,
    onEditModeChange: (Boolean) -> Unit,
) {
    Row(
        modifier =
            Modifier
                .fillMaxWidth(),
        verticalAlignment = Alignment.Top,
    ) {
        Image(
            painter = painterResource(R.drawable.ic_profile),
            contentDescription = "profile",
            modifier =
                Modifier
                    .size(52.dp),
        )

        Column(
            horizontalAlignment = Alignment.Start,
        ) {
            if (isEditMode) {
                ValidateUnderlineTextField(
                    modifier =
                        Modifier
                            .height(77.dp),
                    value = nickname,
                    onCommit = onCommitNickName,
                    placeholder = stringResource(R.string.settings_nickname_placeholder),
                    guideText = stringResource(R.string.settings_nickname_text_filed_guide),
                    validLengthRange = 2..8,
                )
            } else {
                Row(
                    modifier =
                        Modifier
                            .height(52.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Start,
                ) {
                    Spacer(Modifier.width(16.dp))

                    AppText(
                        text = nickname,
                        style = AppTextStyle.T1,
                        color = GrayColor.C500,
                    )

                    Image(
                        painter = painterResource(R.drawable.ic_edit),
                        contentDescription = null,
                        modifier =
                            Modifier
                                .padding(10.dp)
                                .size(24.dp)
                                .noRippleClickable { onEditModeChange(true) },
                    )
                }
            }
        }
    }
}