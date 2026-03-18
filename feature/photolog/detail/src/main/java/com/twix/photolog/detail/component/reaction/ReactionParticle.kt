package com.twix.photolog.detail.component.reaction

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D

data class ReactionParticle(
    val iconRes: Int,
    val animX: Animatable<Float, AnimationVector1D> = Animatable(0f),
    val animY: Animatable<Float, AnimationVector1D> = Animatable(0f),
    val animScale: Animatable<Float, AnimationVector1D> = Animatable(0f),
    val animAlpha: Animatable<Float, AnimationVector1D> = Animatable(1f),
)
