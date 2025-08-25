package com.hihihihi.gureumpage.notification.summary

interface SummaryProvider {
    suspend fun weekly(): Pair<String, String>  // title, body
    suspend fun monthly(): Pair<String, String>
    suspend fun yearly(): Pair<String, String>
}