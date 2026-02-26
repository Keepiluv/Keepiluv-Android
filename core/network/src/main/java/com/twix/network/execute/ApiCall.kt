package com.twix.network.execute

import com.twix.network.model.error.ErrorResponse
import com.twix.result.AppError
import com.twix.result.AppResult
import io.ktor.client.plugins.ResponseException
import io.ktor.client.statement.bodyAsText
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import java.io.IOException
import java.net.SocketTimeoutException
import kotlin.coroutines.cancellation.CancellationException

val errorJson =
    Json {
        ignoreUnknownKeys = true
        isLenient = true
    }

suspend inline fun <T> safeApiCall(crossinline call: suspend () -> T): AppResult<T> =
    try {
        AppResult.Success(call())
    } catch (e: CancellationException) {
        throw e
    } catch (e: ResponseException) {
        val status = e.response.status.value
        val raw = runCatching { e.response.bodyAsText() }.getOrNull()

        val parsed =
            runCatching {
                raw?.let { errorJson.decodeFromString<ErrorResponse>(it) }
            }.getOrNull()

        val code = parsed?.code
        val message = parsed?.message

        val mappedError =
            when {
                status == 401 && code == "G4011" ->
                    AppError.Auth.TokenExpired(status, code, message, raw)

                status == 401 ->
                    AppError.Auth.Unauthorized(status, code, message, raw)

                else ->
                    AppError.Http(status, code, message, raw)
            }

        AppResult.Error(mappedError)
    } catch (e: SocketTimeoutException) {
        AppResult.Error(AppError.Timeout(e))
    } catch (e: IOException) {
        AppResult.Error(AppError.Network(e))
    } catch (e: SerializationException) {
        AppResult.Error(AppError.Serialization(e))
    } catch (e: Throwable) {
        AppResult.Error(AppError.Unknown(e))
    }
