package com.twix.task_certification.certification.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.twix.designsystem.R
import com.twix.designsystem.theme.GrayColor
import com.twix.ui.extension.noRippleClickable

@Composable
internal fun TaskCertificationTopBar(
    onClickClose: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier =
            modifier
                .fillMaxWidth()
                .height(72.dp)
                .background(color = GrayColor.C500),
    ) {
        Image(
            imageVector = ImageVector.vectorResource(R.drawable.ic_close_c100),
            contentDescription = null,
            modifier =
                Modifier
                    .padding(24.dp)
                    .align(Alignment.CenterEnd)
                    .noRippleClickable(onClick = onClickClose),
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun TaskCertificationTopBarPreview() {
    TaskCertificationTopBar({})
}
