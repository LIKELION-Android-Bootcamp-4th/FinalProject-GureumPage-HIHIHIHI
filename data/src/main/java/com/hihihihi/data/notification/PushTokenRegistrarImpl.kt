package com.hihihihi.data.notification

import com.hihihihi.domain.notification.PushTokenRegistrar
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PushTokenRegistrarImpl @Inject constructor() : PushTokenRegistrar {
    override fun upsert(token: String) { }
}