package com.twix.designsystem.components.goal

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.twix.designsystem.R
import com.twix.domain.model.enums.GoalCheckState
import com.twix.ui.extension.noRippleClickable

@Composable
fun GoalCheckIndicator(
    state: GoalCheckState,
    modifier: Modifier = Modifier,
    onClick: (GoalCheckState) -> Unit,
) {
    val meChecked = state == GoalCheckState.ONLY_ME || state == GoalCheckState.BOTH
    val partnerChecked = state == GoalCheckState.ONLY_PARTNER || state == GoalCheckState.BOTH

    Box(modifier = modifier) {
        CheckDot(
            checked = partnerChecked,
            isPartner = true,
            onClick = { onClick(state) },
        )

        CheckDot(
            checked = meChecked,
            isPartner = false,
            modifier = Modifier.offset(x = (-17).dp),
            onClick = { onClick(state) },
        )
    }
}

@Composable
private fun CheckDot(
    modifier: Modifier = Modifier,
    checked: Boolean,
    isPartner: Boolean = false,
    onClick: () -> Unit,
) {
    val res =
        if (isPartner) {
            if (checked) R.drawable.ic_checked_you else R.drawable.ic_unchecked_you
        } else {
            if (checked) R.drawable.ic_checked_me else R.drawable.ic_unchecked_me
        }

    Image(
        painter = painterResource(res),
        contentDescription = null,
        modifier =
            modifier
                .size(28.dp)
                .noRippleClickable(onClick = onClick),
    )
}
