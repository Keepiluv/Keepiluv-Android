package com.twix.datastore.deviceid

import androidx.datastore.core.Serializer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import java.io.InputStream
import java.io.OutputStream

@Serializable
data class DeviceId(
    val deviceId: String = "",
)

internal object DeviceIdSerializer : Serializer<DeviceId> {
    private val json =
        Json {
            ignoreUnknownKeys = true
            isLenient = true
        }

    override val defaultValue: DeviceId
        get() = DeviceId()

    override suspend fun readFrom(input: InputStream): DeviceId =
        try {
            withContext(Dispatchers.IO) {
                json.decodeFromString(
                    deserializer = DeviceId.serializer(),
                    string = input.readBytes().decodeToString(),
                )
            }
        } catch (e: SerializationException) {
            defaultValue
        }

    override suspend fun writeTo(
        t: DeviceId,
        output: OutputStream,
    ) {
        withContext(Dispatchers.IO) {
            output.write(
                json
                    .encodeToString(
                        serializer = DeviceId.serializer(),
                        value = t,
                    ).encodeToByteArray(),
            )
        }
    }
}
