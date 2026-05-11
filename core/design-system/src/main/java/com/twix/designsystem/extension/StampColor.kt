package com.twix.designsystem.extension

import androidx.compose.ui.graphics.Color
import com.twix.designsystem.theme.ChromaticColor
import com.twix.domain.model.enums.StampColor

fun StampColor.toResId(): Color =
    when (this) {
        StampColor.GREEN400 -> ChromaticColor.Green400
        StampColor.BLUE400 -> ChromaticColor.Blue400
        StampColor.YELLOW400 -> ChromaticColor.Yellow400
        StampColor.PINK400 -> ChromaticColor.Pink400
        StampColor.PINK300 -> ChromaticColor.Pink300
        StampColor.PINK200 -> ChromaticColor.Pink200
        StampColor.ORANGE400 -> ChromaticColor.Orange400
        StampColor.PURPLE400 -> ChromaticColor.Purple400
    }
