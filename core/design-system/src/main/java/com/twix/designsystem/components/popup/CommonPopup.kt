package com.twix.designsystem.components.popup

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import com.twix.designsystem.components.text.AppText
import com.twix.designsystem.theme.GrayColor
import com.twix.domain.model.enums.AppTextStyle
import com.twix.ui.extension.noRippleClickable

@Composable
fun CommonPopup(
    visible: Boolean,
    anchorOffset: IntOffset,
    onDismiss: () -> Unit,
    content: @Composable () -> Unit,
) {
    if (!visible) return

    Popup(
        alignment = Alignment.TopStart,
        offset = anchorOffset,
        onDismissRequest = onDismiss,
        properties =
            PopupProperties(
                focusable = true,
                dismissOnBackPress = true,
                dismissOnClickOutside = true,
            ),
    ) {
        content()
    }
}

@Composable
fun CommonPopupItem(
    text: String,
    onClick: () -> Unit,
) {
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .noRippleClickable { onClick() }
                .padding(horizontal = 16.dp, vertical = 12.dp),
    ) {
        AppText(
            text = text,
            style = AppTextStyle.B2,
            color = GrayColor.C500,
            textAlign = TextAlign.Start,
        )
    }
}

@Composable
fun CommonPopupDivider() {
    HorizontalDivider(thickness = 1.dp, color = GrayColor.C500)
}
