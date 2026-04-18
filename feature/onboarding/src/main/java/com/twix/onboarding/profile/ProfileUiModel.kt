package com.twix.onboarding.profile

import androidx.compose.runtime.Immutable
import com.twix.domain.model.nickname.NickName

@Immutable
data class ProfileUiModel(
    val nickname: String = "",
    val isValid: Boolean = false,
) {
    fun updateNickname(value: String): ProfileUiModel =
        NickName
            .create(value)
            .fold(
                onSuccess = { copy(nickname = value, isValid = true) },
                onFailure = { copy(nickname = value, isValid = false) },
            )
}
