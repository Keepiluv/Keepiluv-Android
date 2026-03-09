package com.twix.designsystem.components.stats

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.twix.designsystem.components.text.AppText
import com.twix.designsystem.theme.CommonColor
import com.twix.designsystem.theme.DimmedColor
import com.twix.designsystem.theme.GrayColor
import com.twix.domain.model.enums.AppTextStyle
import com.twix.domain.model.stats.detail.CompletedDate
import com.twix.ui.extension.noRippleClickable
import java.time.LocalDate

@Composable
fun PictureDayCell(
    date: LocalDate,
    completed: CompletedDate?,
    onDateSelected: (LocalDate) -> Unit,
    modifier: Modifier = Modifier,
) {
    val showBackgroundCard = completed?.myImageUrl != null && completed.partnerImageUrl != null
    val hasImage = completed?.partnerImageUrl != null || completed?.myImageUrl != null

    val textColor = if (hasImage) CommonColor.White else GrayColor.C500
    val borderColor = if (showBackgroundCard) CommonColor.White else GrayColor.C400
    val cornerShape = RoundedCornerShape(7.dp)
    val context = LocalContext.current

    Box(
        modifier =
            modifier
                .aspectRatio(1f)
                .noRippleClickable { onDateSelected(date) },
        contentAlignment = Alignment.Center,
    ) {
        if (showBackgroundCard) {
            Box(
                modifier =
                    Modifier
                        .size(36.dp)
                        .rotate(-16f)
                        .border(1.dp, GrayColor.C400, cornerShape)
                        .background(CommonColor.White, cornerShape),
            )
        }

        Box(
            modifier =
                Modifier
                    .size(36.dp)
                    .clip(cornerShape)
                    .then(
                        if (hasImage) {
                            Modifier.border((1.2).dp, borderColor, cornerShape)
                        } else {
                            Modifier
                        },
                    ),
            contentAlignment = Alignment.Center,
        ) {
            val displayImageUrl = completed?.partnerImageUrl ?: completed?.myImageUrl
            if (displayImageUrl != null) {
                val imageRequest =
                    remember(displayImageUrl, context) {
                        ImageRequest
                            .Builder(context)
                            .data(displayImageUrl)
                            .crossfade(true)
                            .build()
                    }

                AsyncImage(
                    model = imageRequest,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize(),
                )

                Box(
                    modifier =
                        Modifier
                            .fillMaxSize()
                            .background(DimmedColor.D020),
                )
            }

            AppText(
                text = date.dayOfMonth.toString(),
                style = AppTextStyle.B3,
                color = textColor,
                textAlign = TextAlign.Center,
            )
        }
    }
}
