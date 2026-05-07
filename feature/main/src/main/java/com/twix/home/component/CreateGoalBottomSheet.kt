package com.twix.home.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.twix.designsystem.R
import com.twix.designsystem.components.bottomsheet.AdaptiveSheetList
import com.twix.designsystem.components.goal.GoalCardFrame
import com.twix.designsystem.components.text.AppText
import com.twix.designsystem.extension.stringResId
import com.twix.designsystem.theme.CommonColor
import com.twix.designsystem.theme.GrayColor
import com.twix.designsystem.theme.TwixTheme
import com.twix.domain.model.enums.AppTextStyle
import com.twix.domain.model.goal.RecommendedGoalPresets
import com.twix.home.model.CreateGoalSheetItem
import com.twix.ui.extension.noRippleClickable

@Composable
fun CreateGoalBottomSheet(
    items: List<CreateGoalSheetItem>,
    onDirectAddClick: () -> Unit,
    onPresetClick: (CreateGoalSheetItem.Preset) -> Unit,
) {
    Column(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(top = 13.5.dp)
                .padding(horizontal = 20.dp),
    ) {
        CreateGoalHeader()

        Spacer(Modifier.height(33.5.dp))

        AdaptiveSheetList(
            items = items,
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(bottom = 16.dp),
        ) { item ->
            when (item) {
                CreateGoalSheetItem.DirectAdd -> {
                    GoalCardFrame(
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .noRippleClickable(onClick = onDirectAddClick),
                        goalName = stringResource(R.string.action_create_goal),
                        icon = {
                            Image(
                                painter = painterResource(R.drawable.ic_circle_border_add),
                                contentDescription = null,
                                modifier = Modifier.size(32.dp),
                            )
                        },
                        right = { CircleArrowButton(onClick = onDirectAddClick) },
                        content = {},
                    )
                }

                is CreateGoalSheetItem.Preset -> {
                    GoalCardFrame(
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .noRippleClickable(onClick = { onPresetClick(item) }),
                        goalName = item.title,
                        goalIcon = item.icon,
                        right = { CircleArrowButton(onClick = { onPresetClick(item) }) },
                        content = {},
                    )
                }
            }
        }
    }
}

@Composable
private fun CreateGoalHeader() {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        AppText(
            text = stringResource(R.string.create_goal_header),
            style = AppTextStyle.H4Brand,
            color = GrayColor.C500,
        )

        AppText(
            text = stringResource(R.string.create_goal_content),
            style = AppTextStyle.B2,
            color = GrayColor.C400,
        )
    }
}

@Composable
private fun CircleArrowButton(onClick: () -> Unit) {
    Surface(
        modifier =
            Modifier
                .size(28.dp)
                .noRippleClickable(onClick = onClick),
        shape = CircleShape,
        color = GrayColor.C500,
    ) {
        Image(
            painter = painterResource(R.drawable.ic_arrow3_right),
            contentDescription = null,
            colorFilter = ColorFilter.tint(CommonColor.White),
            modifier =
                Modifier
                    .padding(3.dp)
                    .size(22.dp),
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun Preview() {
    TwixTheme {
        val createGoalSheetItems =
            listOf<CreateGoalSheetItem>(CreateGoalSheetItem.DirectAdd) +
                RecommendedGoalPresets.items.map { preset ->
                    CreateGoalSheetItem.Preset(
                        presetId = preset.id,
                        title = stringResource(preset.titleKey.stringResId()),
                        icon = preset.icon,
                    )
                }

        CreateGoalBottomSheet(
            items = createGoalSheetItems,
            onDirectAddClick = {},
            onPresetClick = {},
        )
    }
}
