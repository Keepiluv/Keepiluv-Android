package com.twix.designsystem.theme

import androidx.compose.runtime.Immutable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import com.twix.designsystem.R

val LaundryGothicFamily =
    FontFamily(
        Font(R.font.laundry_gothic_regular, FontWeight.Normal),
        Font(R.font.laundry_gothic_bold, FontWeight.Bold),
    )

val NanumSquareNeoFamily =
    FontFamily(
        Font(R.font.nanum_square_neo_light, FontWeight.Light),
        Font(R.font.nanum_square_neo_regular, FontWeight.Normal),
        Font(R.font.nanum_square_neo_bold, FontWeight.Bold),
        Font(R.font.nanum_square_neo_extra_bold, FontWeight.ExtraBold),
    )

@Immutable
data class AppTypography(
    val h1: TextStyle,
    val h2: TextStyle,
    val h3: TextStyle,
    val h3Brand: TextStyle,
    val h4Brand: TextStyle,
    val t1: TextStyle,
    val t2: TextStyle,
    val t3: TextStyle,
    val b1: TextStyle,
    val b2: TextStyle,
    val b3: TextStyle,
    val b4: TextStyle,
    val c1: TextStyle,
    val c2: TextStyle,
)

fun provideAppTypography(): AppTypography =
    AppTypography(
        h1 =
            TextStyle(
                fontFamily = NanumSquareNeoFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 28.sp,
                lineHeight = lineHeightPercent(28f, 140f),
                letterSpacing = letterSpacingPercent(28f, -1f),
            ),
        h2 =
            TextStyle(
                fontFamily = NanumSquareNeoFamily,
                fontWeight = FontWeight.Normal,
                fontSize = 24.sp,
                lineHeight = lineHeightPercent(24f, 140f),
                letterSpacing = letterSpacingPercent(24f, -1f),
            ),
        h3 =
            TextStyle(
                fontFamily = NanumSquareNeoFamily,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 22.sp,
                lineHeight = lineHeightPercent(22f, 140f),
                letterSpacing = letterSpacingPercent(22f, -1f),
            ),
        h3Brand =
            TextStyle(
                fontFamily = LaundryGothicFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                lineHeight = lineHeightPercent(20f, 140f),
                letterSpacing = letterSpacingPercent(20f, -2f),
            ),
        h4Brand =
            TextStyle(
                fontFamily = LaundryGothicFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                lineHeight = lineHeightPercent(20f, 140f),
                letterSpacing = letterSpacingPercent(20f, -2f),
            ),
        t1 =
            TextStyle(
                fontFamily = NanumSquareNeoFamily,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 18.sp,
                lineHeight = lineHeightPercent(18f, 150f),
                letterSpacing = letterSpacingPercent(18f, -2f),
            ),
        t2 =
            TextStyle(
                fontFamily = NanumSquareNeoFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                lineHeight = lineHeightPercent(16f, 150f),
                letterSpacing = letterSpacingPercent(16f, -2f),
            ),
        t3 =
            TextStyle(
                fontFamily = NanumSquareNeoFamily,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 14.sp,
                lineHeight = lineHeightPercent(14f, 150f),
                letterSpacing = letterSpacingPercent(14f, -1f),
            ),
        b1 =
            TextStyle(
                fontFamily = NanumSquareNeoFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                lineHeight = lineHeightPercent(14f, 150f),
                letterSpacing = letterSpacingPercent(14f, -2.5f),
            ),
        b2 =
            TextStyle(
                fontFamily = NanumSquareNeoFamily,
                fontWeight = FontWeight.Normal,
                fontSize = 14.sp,
                lineHeight = lineHeightPercent(14f, 150f),
                letterSpacing = letterSpacingPercent(14f, -2.5f),
            ),
        b3 =
            TextStyle(
                fontFamily = NanumSquareNeoFamily,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 12.sp,
                lineHeight = lineHeightPercent(12f, 150f),
                letterSpacing = letterSpacingPercent(12f, -1f),
            ),
        b4 =
            TextStyle(
                fontFamily = NanumSquareNeoFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
                lineHeight = lineHeightPercent(12f, 150f),
                letterSpacing = letterSpacingPercent(12f, -2.5f),
            ),
        c1 =
            TextStyle(
                fontFamily = NanumSquareNeoFamily,
                fontWeight = FontWeight.Normal,
                fontSize = 12.sp,
                lineHeight = lineHeightPercent(12f, 150f),
                letterSpacing = letterSpacingPercent(12f, -2.5f),
            ),
        c2 =
            TextStyle(
                fontFamily = NanumSquareNeoFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 11.sp,
                lineHeight = lineHeightPercent(11f, 150f),
                letterSpacing = letterSpacingPercent(11f, -2.5f),
            ),
    )

/**
 * 행간 계산하는 메서드
 * */
private fun lineHeightPercent(
    fontSizeSp: Float,
    percent: Float,
): TextUnit = (fontSizeSp * (percent / 100f)).sp

/**
 * 자간 계산하는 메서드
 * */
private fun letterSpacingPercent(
    fontSizeSp: Float,
    percent: Float,
): TextUnit = (fontSizeSp * (percent / 100f)).sp
