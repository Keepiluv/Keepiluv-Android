package com.twix.designsystem.components.photolog

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.twix.designsystem.R
import com.twix.designsystem.components.text.AppText
import com.twix.designsystem.theme.CommonColor
import com.twix.designsystem.theme.GrayColor
import com.twix.designsystem.theme.TwixTheme
import com.twix.domain.model.enums.AppTextStyle
import com.twix.domain.model.enums.BetweenUs

@Composable
fun ForegroundCard(
    isCertificated: Boolean,
    nickName: String,
    imageUrl: String?,
    comment: String?,
    currentShow: BetweenUs,
    rotation: Float,
) {
    PhotologCard(
        rotation = rotation,
        background = CommonColor.White,
        borderColor = GrayColor.C500,
    ) {
        if (isCertificated) {
            CertificatedCard(imageUrl, comment)
        } else {
            AppText(
                text =
                    when (currentShow) {
                        BetweenUs.ME -> stringResource(R.string.photolog_detail_upload_guide)
                        BetweenUs.PARTNER ->
                            stringResource(R.string.partner_not_photolog).format(
                                nickName,
                            )
                    },
                style = AppTextStyle.H2,
                color = GrayColor.C500,
                textAlign = TextAlign.Center,
            )
        }
    }
}

@Preview
@Composable
private fun ForegroundCardPreview() {
    TwixTheme {
        ForegroundCard(
            isCertificated = false,
            nickName = "닉네임",
            imageUrl = "https://picsum.photos/200/300",
            comment = null,
            currentShow = BetweenUs.ME,
            rotation = -8f,
        )
    }
}
