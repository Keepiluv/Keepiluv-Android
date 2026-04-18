package com.twix.ui.keyboard

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.ime
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.platform.LocalDensity

/**
 * 현재 소프트 키보드(IME)의 열림/닫힘 상태를 [Keyboard] 값으로 제공하는 Composable 유틸 함수.
 *
 * [WindowInsets.ime] 의 bottom inset 값을 사용하여
 * 키보드가 화면을 밀어 올리고 있는지 여부를 감지합니다.
 *
 * - inset > 0  → [Keyboard.Opened]
 * - inset == 0 → [Keyboard.Closed]
 *
 * @return 현재 키보드 상태를 나타내는 [State]<[Keyboard]>
 */
@Composable
fun keyboardAsState(): State<Keyboard> {
    val density = LocalDensity.current
    // 1. ime 인셋의 하단 높이를 관찰 가능한 상태로 가져옵니다.
    val imeBottom = WindowInsets.ime.getBottom(density)

    // 2. 이 값이 변할 때마다 Keyboard 상태를 계산합니다.
    val keyboardState =
        remember(imeBottom) {
            if (imeBottom > 0) Keyboard.Opened else Keyboard.Closed
        }

    return rememberUpdatedState(keyboardState)
}
