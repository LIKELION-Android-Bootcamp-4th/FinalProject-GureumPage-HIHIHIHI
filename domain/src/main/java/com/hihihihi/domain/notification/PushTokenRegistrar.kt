package com.hihihihi.domain.notification

interface PushTokenRegistrar {
    fun upsert(token: String)
}