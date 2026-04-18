package com.twix.designsystem.components.goal

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.twix.designsystem.components.text.AppText
import com.twix.designsystem.extension.toRes
import com.twix.designsystem.theme.CommonColor
import com.twix.designsystem.theme.GrayColor
import com.twix.domain.model.enums.AppTextStyle
import com.twix.domain.model.enums.GoalIconType

@Composable
fun GoalCardFrame(
    modifier: Modifier = Modifier,
    goalName: String,
    goalIcon: GoalIconType,
    right: @Composable RowScope.() -> Unit,
    content: @Composable ColumnScope.() -> Unit,
) {
    val shape = RoundedCornerShape(16.dp)

    Column(
        modifier =
            modifier
                .fillMaxWidth()
                .clip(shape)
                .border(1.dp, GrayColor.C500, shape)
                .background(CommonColor.White),
    ) {
        // Header
        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Image(
                painter = painterResource(goalIcon.toRes()),
                contentDescription = null,
                modifier = Modifier.size(22.dp),
            )

            Spacer(Modifier.width(10.dp))

            AppText(
                text = goalName,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = AppTextStyle.T2,
                color = GrayColor.C500,
                modifier = Modifier.weight(1f),
            )

            Spacer(Modifier.width(10.dp))

            // 헤더의 오른쪽 아이콘 홈이면 체크, 수정이면 점 세개
            right()
        }

        content()
    }
}
