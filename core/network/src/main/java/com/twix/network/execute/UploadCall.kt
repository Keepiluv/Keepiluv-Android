package com.twix.network.execute

import com.twix.result.AppError
import com.twix.result.AppResult
import io.ktor.client.plugins.ResponseException
import io.ktor.client.statement.bodyAsText
import kotlinx.io.IOException
import kotlinx.serialization.SerializationException
import java.net.SocketTimeoutException
import kotlin.coroutines.cancellation.CancellationException

suspend inline fun <T> safeUploadCall(crossinline call: suspend () -> T): AppResult<T> =
    try {
        AppResult.Success(call())
    } catch (e: CancellationException) {
        throw e
    } catch (e: ResponseException) {
        val status = e.response.status.value
        val raw = runCatching { e.response.bodyAsText() }.getOrNull()

        AppResult.Error(
            AppError.Http(
                status = status,
                code = null,
                message = null,
                rawBody = raw,
            ),
        )
    } catch (e: SocketTimeoutException) {
        AppResult.Error(AppError.Timeout(e))
    } catch (e: IOException) {
        AppResult.Error(AppError.Network(e))
    } catch (e: SerializationException) {
        AppResult.Error(AppError.Serialization(e))
    } catch (e: Throwable) {
        AppResult.Error(AppError.Unknown(e))
    }
