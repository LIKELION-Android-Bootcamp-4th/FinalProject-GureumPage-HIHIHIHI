package com.hihihihi.domain.repository

import android.app.Activity

interface NaverAuthRepository {
    suspend fun login(activity: Activity): String // accessToken 리턴
}