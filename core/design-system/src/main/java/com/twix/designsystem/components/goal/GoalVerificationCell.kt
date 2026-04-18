package com.twix.designsystem.components.goal

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.twix.designsystem.extension.toRes
import com.twix.designsystem.theme.GrayColor
import com.twix.domain.model.goal.GoalVerification
import com.twix.ui.extension.noRippleClickable

@Composable
fun GoalVerificationCell(
    modifier: Modifier = Modifier,
    verification: GoalVerification?,
    emptyContent: @Composable () -> Unit,
    onClick: (() -> Unit)? = null,
) {
    Box(
        modifier =
            modifier
                .aspectRatio(174f / 136f)
                .background(GrayColor.C050)
                .let { if (onClick != null) it.noRippleClickable { onClick() } else it },
        contentAlignment = Alignment.Center,
    ) {
        if (verification == null) {
            emptyContent()
        } else {
            AsyncImage(
                model =
                    ImageRequest
                        .Builder(LocalContext.current)
                        .data(verification.imageUrl)
                        .crossfade(true)
                        .build(),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize(),
            )

            Box(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomEnd)
                        .padding(6.dp),
                contentAlignment = Alignment.BottomEnd,
            ) {
                verification.reaction?.let {
                    Image(
                        painter = painterResource(it.toRes()),
                        contentDescription = null,
                        modifier = Modifier.size(32.dp),
                    )
                }
            }
        }
    }
}
