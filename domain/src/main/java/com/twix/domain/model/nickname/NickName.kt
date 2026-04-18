package com.twix.domain.model.nickname

@JvmInline
value class NickName(
    val value: String,
) {
    companion object {
        fun create(value: String): Result<NickName> =
            runCatching {
                if (value.length !in MIN_LENGTH..MAX_LENGTH) {
                    throw NickNameException.InvalidLengthException
                }
                NickName(value)
            }

        private const val MIN_LENGTH = 2
        private const val MAX_LENGTH = 8
    }
}
