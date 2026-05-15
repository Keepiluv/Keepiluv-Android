package com.twix.result

fun <T> AppResult<T>.errorOrNull() =
    when (this) {
        is AppResult.Error -> error
        is AppResult.Success -> null
    }
