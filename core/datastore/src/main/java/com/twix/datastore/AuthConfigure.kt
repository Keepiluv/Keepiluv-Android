package com.twix.datastore

import androidx.datastore.core.Serializer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import java.io.InputStream
import java.io.OutputStream

@Serializable
internal data class AuthConfigure(
    val accessToken: String = "",
    val refreshToken: String = "",
)

internal object AuthConfigureSerializer : Serializer<AuthConfigure> {
    private val json =
        Json {
            ignoreUnknownKeys = true
            isLenient = true
        }

    override val defaultValue: AuthConfigure
        get() = AuthConfigure()

    override suspend fun readFrom(input: InputStream): AuthConfigure =
        try {
            withContext(Dispatchers.IO) {
                json.decodeFromString(
                    deserializer = AuthConfigure.serializer(),
                    string = input.readBytes().decodeToString(),
                )
            }
        } catch (e: SerializationException) {
            defaultValue
        }

    override suspend fun writeTo(
        t: AuthConfigure,
        output: OutputStream,
    ) {
        withContext(Dispatchers.IO) {
            output.write(
                json
                    .encodeToString(
                        serializer = AuthConfigure.serializer(),
                        value = t,
                    ).encodeToByteArray(),
            )
        }
    }
}
