package com.twix.task_certification.detail.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.twix.designsystem.components.comment.CommentTextField
import com.twix.designsystem.components.comment.model.CommentUiModel
import com.twix.designsystem.theme.TwixTheme

@Composable
internal fun CertificatedCard(
    imageUrl: String?,
    comment: String?,
) {
    Box(Modifier.fillMaxSize()) {
        AsyncImage(
            model =
                ImageRequest
                    .Builder(LocalContext.current)
                    .data(imageUrl)
                    .crossfade(true)
                    .build(),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize(),
        )
        if (comment?.isNotEmpty() == true) {
            CommentTextField(
                uiModel = CommentUiModel(comment),
                enabled = false,
                modifier =
                    Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 28.dp),
            )
        }
    }
}

@Preview
@Composable
fun CertificatedCardPreview() {
    TwixTheme {
        CertificatedCard(
            imageUrl = "https://picsum.photos/200/300",
            comment = "아이수크림",
        )
    }
}
