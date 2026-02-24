package com.twix.onboarding.dday.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import com.twix.designsystem.R
import com.twix.ui.extension.noRippleClickable

@Composable
fun DdayTopBar(
    navigateToBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier =
            modifier
                .fillMaxWidth()
                .height(72.dp)
                .padding(horizontal = 10.dp, vertical = 14.dp),
        contentAlignment = Alignment.CenterStart,
    ) {
        Image(
            imageVector = ImageVector.vectorResource(R.drawable.ic_arrow_m_left),
            contentDescription = null,
            modifier =
                Modifier
                    .size(44.dp)
                    .noRippleClickable(onClick = navigateToBack),
        )
    }
}
