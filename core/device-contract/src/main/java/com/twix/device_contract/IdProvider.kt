package com.twix.device_contract

interface IdProvider {
    suspend fun getOrCreateDeviceId(): String

    suspend fun clear()
}
