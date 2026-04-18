package com.twix.designsystem.extension

import com.twix.designsystem.R
import com.twix.domain.model.enums.StampType

fun StampType.toBorderRes(): Int =
    when (this) {
        StampType.CLOVER -> R.drawable.ic_stamp_clover_border
        StampType.FLOWER -> R.drawable.ic_stamp_flower_border
        StampType.HEART -> R.drawable.ic_stamp_heart_border
        StampType.MOON -> R.drawable.ic_stamp_moon_border
        StampType.NOTE -> R.drawable.ic_stamp_note_border
    }

fun StampType.toBackgroundRes(): Int =
    when (this) {
        StampType.CLOVER -> R.drawable.ic_stamp_clover
        StampType.FLOWER -> R.drawable.ic_stamp_flower
        StampType.HEART -> R.drawable.ic_stamp_heart
        StampType.MOON -> R.drawable.ic_stamp_moon
        StampType.NOTE -> R.drawable.ic_stamp_note
    }
