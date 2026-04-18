package com.twix.main.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.twix.designsystem.components.text.AppText
import com.twix.designsystem.theme.CommonColor
import com.twix.designsystem.theme.GrayColor
import com.twix.domain.model.enums.AppTextStyle
import com.twix.main.model.MainTab

@Composable
fun MainBottomBar(
    selectedTab: MainTab,
    onTabClick: (MainTab) -> Unit,
) {
    Column {
        HorizontalDivider(thickness = 1.dp, color = GrayColor.C100)

        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .height(58.dp)
                    .background(CommonColor.White),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            MainTab.entries.forEach { tab ->
                MainBottomBarItem(
                    modifier = Modifier.weight(1f),
                    tab = tab,
                    selected = selectedTab == tab,
                    onClick = { onTabClick(tab) },
                )
            }
        }
    }
}

@Composable
private fun MainBottomBarItem(
    modifier: Modifier = Modifier,
    tab: MainTab,
    selected: Boolean,
    onClick: () -> Unit,
) {
    val haptic = LocalHapticFeedback.current
    val interactionSource = remember { MutableInteractionSource() }
    val icon = if (selected) tab.selectedIcon else tab.unselectedIcon

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Image(
            painter = painterResource(icon),
            contentDescription = null,
            modifier =
                Modifier
                    .size(24.dp)
                    .clickable(
                        indication = null,
                        interactionSource = interactionSource,
                        onClick = {
                            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            onClick()
                        },
                    ),
        )

        AppText(
            text = stringResource(tab.title),
            color = GrayColor.C500,
            style = AppTextStyle.C2,
        )
    }
}
