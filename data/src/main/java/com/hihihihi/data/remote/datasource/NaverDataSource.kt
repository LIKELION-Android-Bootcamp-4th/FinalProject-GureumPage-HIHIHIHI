package com.hihihihi.data.remote.datasource

import android.app.Activity

interface NaverDataSource {
    suspend fun signIn(activity: Activity): String
}