package com.twix.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.dataStore
import com.twix.datastore.deviceid.DeviceId
import com.twix.datastore.deviceid.DeviceIdSerializer

internal val Context.authDataStore: DataStore<AuthConfigure> by dataStore(
    fileName = "auth-configure.json",
    serializer = AuthConfigureSerializer,
)

internal val Context.deviceIdDataStore: DataStore<DeviceId> by dataStore(
    fileName = "device-id.json",
    serializer = DeviceIdSerializer,
)
