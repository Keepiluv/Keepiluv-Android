package com.twix.domain.model.invitecode

@JvmInline
value class InviteCode(
    val value: String,
) {
    companion object {
        fun create(value: String): Result<InviteCode> =
            runCatching {
                if (value.length != INVITE_CODE_LENGTH) {
                    throw InviteCodeException.InvalidLengthException
                }
                InviteCode(value)
            }

        const val INVITE_CODE_LENGTH = 8
    }
}
