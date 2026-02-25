package com.twix.datastore.deviceid

import android.content.Context
import androidx.datastore.core.DataStore
import com.twix.datastore.deviceIdDataStore
import com.twix.device_contract.IdProvider
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.util.UUID

class DeviceIdProvider(
    context: Context,
) : IdProvider {
    private val dataStore: DataStore<DeviceId> = context.deviceIdDataStore
    private val mutex = Mutex()

    override suspend fun getOrCreateDeviceId(): String {
        val current =
            dataStore.data
                .first()
                .deviceId
                .trim()
        if (current.isNotEmpty()) return current

        return mutex.withLock {
            val rechecked =
                dataStore.data
                    .first()
                    .deviceId
                    .trim()
            if (rechecked.isNotEmpty()) return@withLock rechecked

            val newId = "twix-${UUID.randomUUID()}"

            dataStore.updateData { currentValue ->
                currentValue.copy(deviceId = newId)
            }

            newId
        }
    }

    override suspend fun clear() {
        dataStore.updateData { currentValue ->
            currentValue.copy(deviceId = "")
        }
    }
}
