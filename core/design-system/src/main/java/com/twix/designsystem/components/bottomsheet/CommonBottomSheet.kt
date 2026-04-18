package com.twix.designsystem.components.bottomsheet

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.twix.designsystem.R
import com.twix.designsystem.components.bottomsheet.model.CommonBottomSheetConfig
import com.twix.designsystem.components.text.AppText
import com.twix.designsystem.theme.CommonColor
import com.twix.designsystem.theme.GrayColor
import com.twix.designsystem.theme.TwixTheme
import com.twix.domain.model.enums.AppTextStyle
import com.twix.ui.extension.noRippleClickable
import kotlinx.coroutines.android.awaitFrame
import kotlinx.coroutines.delay

@Composable
fun CommonBottomSheet(
    modifier: Modifier = Modifier,
    visible: Boolean,
    config: CommonBottomSheetConfig = CommonBottomSheetConfig(),
    onDismissRequest: () -> Unit,
    content: @Composable ColumnScope.() -> Unit,
) {
    // 애니메이션에 사용할 내부 visible
    var internalVisible by remember { mutableStateOf(false) }
    // 실제 바텀시트 렌더링을 결정하는 상태 변수. 이 값이 없으면 visible이 곧바로 false로 변해서 애니메이션 없이 사라짐
    var rendering by remember { mutableStateOf(false) }

    LaunchedEffect(visible) {
        if (visible) {
            rendering = true
            awaitFrame() // 의도적으로 기다려서 바텀시트 렌더링 애니메이션이 보이도록 유도
            internalVisible = true
        } else {
            internalVisible = false
            delay(200)
            rendering = false
        }
    }

    if (!rendering) return

    BackHandler(enabled = internalVisible) { onDismissRequest() }

    BoxWithConstraints(
        modifier =
            Modifier
                .fillMaxSize()
                .then(modifier),
    ) {
        val sheetMaxHeight = maxHeight * config.maxHeightFraction

        BottomSheetScrim(
            visible = internalVisible,
            config = config,
            onDismissRequest = onDismissRequest,
        )

        BottomSheetContent(
            modifier =
                Modifier
                    .align(Alignment.BottomCenter),
            visible = internalVisible,
            config = config,
            sheetMaxHeight = sheetMaxHeight,
            content = content,
        )
    }
}

@Composable
private fun BottomSheetScrim(
    visible: Boolean,
    config: CommonBottomSheetConfig,
    onDismissRequest: () -> Unit,
) {
    val fadeDuration = 160

    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(animationSpec = tween(fadeDuration)),
        exit = fadeOut(animationSpec = tween(fadeDuration)),
        modifier = Modifier.fillMaxSize(),
    ) {
        Box(
            modifier =
                Modifier
                    .fillMaxSize()
                    .background(config.scrimColor)
                    .noRippleClickable(
                        enabled = config.dismissOnScrimClick,
                        onClick = onDismissRequest,
                    ),
        )
    }
}

@Composable
private fun BottomSheetContent(
    modifier: Modifier,
    visible: Boolean,
    config: CommonBottomSheetConfig,
    sheetMaxHeight: Dp,
    content: @Composable ColumnScope.() -> Unit,
) {
    AnimatedVisibility(
        visible = visible,
        enter =
            slideInVertically(
                animationSpec = tween(220),
                initialOffsetY = { fullHeight -> fullHeight },
            ) + fadeIn(animationSpec = tween(140)),
        exit =
            slideOutVertically(
                animationSpec = tween(180),
                targetOffsetY = { fullHeight -> fullHeight },
            ) + fadeOut(animationSpec = tween(120)),
        modifier =
            modifier
                .fillMaxWidth(),
    ) {
        Surface(
            shape = config.shape,
            tonalElevation = 0.dp,
            shadowElevation = 16.dp,
            color = CommonColor.White,
            modifier =
                Modifier
                    .fillMaxWidth()
                    .heightIn(max = sheetMaxHeight),
        ) {
            Column(
                modifier =
                    Modifier
                        .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                if (config.showHandle) {
                    SheetHandle(
                        modifier =
                            Modifier
                                .align(Alignment.CenterHorizontally),
                    )
                } else {
                    Spacer(Modifier.height(28.dp))
                }

                content()
            }
        }
    }
}

@Composable
private fun SheetHandle(modifier: Modifier = Modifier) {
    Box(
        modifier =
            modifier
                .padding(vertical = 11.dp)
                .width(44.dp)
                .height(6.dp)
                .background(
                    color = GrayColor.C100,
                    shape = RoundedCornerShape(2.55.dp),
                ),
    )
}

@Preview(showBackground = true)
@Composable
private fun Preview() {
    data class TempItem(
        val res: Int = R.drawable.ic_toast_heart,
        val title: String = "임시 아이템입니다.",
        val action: () -> Unit = {},
    )

    TwixTheme {
        var showBottomSheet by remember { mutableStateOf(false) }
        val list =
            remember {
                listOf(
                    TempItem(),
                    TempItem(),
                    TempItem(),
                    TempItem(),
                    TempItem(),
                    TempItem(),
                    TempItem(),
                    TempItem(),
                    TempItem(),
                    TempItem(),
                    TempItem(),
                    TempItem(),
                    TempItem(),
                    TempItem(),
                    TempItem(),
                )
            }

        Box(
            modifier =
                Modifier
                    .fillMaxSize()
                    .background(CommonColor.White),
            contentAlignment = Alignment.Center,
        ) {
            AppText(
                text = "바텀시트 보기",
                style = AppTextStyle.B1,
                color = GrayColor.C500,
                modifier = Modifier.clickable { showBottomSheet = true },
            )

            CommonBottomSheet(
                config =
                    CommonBottomSheetConfig(
                        showHandle = true,
                    ),
                visible = showBottomSheet,
                onDismissRequest = { showBottomSheet = false },
                content = {
                    AdaptiveSheetList<TempItem>(
                        modifier = Modifier.padding(horizontal = 20.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        items = list,
                        itemContent = {
                            Row(
                                modifier =
                                    Modifier
                                        .fillMaxWidth()
                                        .border(1.dp, GrayColor.C500, RoundedCornerShape(12.dp))
                                        .padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Image(
                                    painter = painterResource(it.res),
                                    contentDescription = "temp icon",
                                    modifier = Modifier.size(32.dp),
                                )

                                Spacer(Modifier.width(4.dp))

                                AppText(
                                    text = it.title,
                                    style = AppTextStyle.T2,
                                    color = GrayColor.C500,
                                    modifier = Modifier.weight(1f),
                                )

                                Image(
                                    painter = painterResource(it.res),
                                    contentDescription = "temp icon",
                                    modifier = Modifier.size(32.dp),
                                )
                            }
                        },
                    )
                },
            )
        }
    }
}
