package com.twix.goal_editor.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.twix.designsystem.R
import com.twix.designsystem.extension.toResId
import com.twix.designsystem.theme.GrayColor
import com.twix.designsystem.theme.TwixTheme
import com.twix.domain.model.enums.GoalIconType
import com.twix.ui.extension.noRippleClickable

@Composable
fun EmojiPicker(
    icon: GoalIconType,
    onClick: () -> Unit,
) {
    Box {
        Box(
            modifier =
                Modifier
                    .size(108.dp)
                    .clip(CircleShape)
                    .background(GrayColor.C050)
                    .border(1.dp, GrayColor.C500, CircleShape)
                    .noRippleClickable(onClick = onClick),
            contentAlignment = Alignment.Center,
        ) {
            Image(
                painter = painterResource(icon.toResId()),
                contentDescription = "emoji",
                modifier =
                    Modifier
                        .size(56.dp),
            )
        }

        Image(
            painter = painterResource(R.drawable.ic_refresh_black),
            contentDescription = "refresh",
            modifier =
                Modifier
                    .size(28.dp)
                    .align(Alignment.BottomEnd),
        )
    }
}

@Preview(showSystemUi = true, showBackground = true)
@Composable
private fun Preview() {
    TwixTheme {
        EmojiPicker(
            icon = GoalIconType.DEFAULT,
            onClick = {},
        )
    }
}
