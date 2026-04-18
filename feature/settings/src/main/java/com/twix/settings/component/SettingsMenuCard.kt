package com.twix.settings.component

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.twix.designsystem.components.text.AppText
import com.twix.designsystem.theme.GrayColor
import com.twix.domain.model.enums.AppTextStyle
import com.twix.ui.extension.noRippleClickable

@Composable
fun SettingsMenuFrame(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit,
) {
    Column(
        modifier =
            modifier
                .fillMaxWidth()
                .border(1.dp, GrayColor.C500, RoundedCornerShape(16.dp)),
        horizontalAlignment = Alignment.Start,
    ) {
        content()
    }
}

@Composable
fun SettingsMenuItem(
    @DrawableRes resId: Int? = null,
    title: String,
    right: (@Composable () -> Unit)? = null,
    onClick: () -> Unit = {},
) {
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .noRippleClickable { onClick() }
                .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        resId?.let {
            Image(
                painter = painterResource(resId),
                contentDescription = "menu",
                modifier =
                    Modifier
                        .size(24.dp),
            )
            Spacer(Modifier.width(8.dp))
        }

        AppText(
            text = title,
            style = AppTextStyle.B1,
            color = GrayColor.C500,
            modifier =
                Modifier
                    .padding(vertical = 5.5.dp),
        )

        Spacer(Modifier.weight(1f))

        right?.invoke()
    }
}
