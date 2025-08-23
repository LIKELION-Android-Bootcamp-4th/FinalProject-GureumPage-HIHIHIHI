package com.hihihihi.gureumpage.notification.schema

import android.net.Uri
import androidx.core.net.toUri

data class PushPayload(
    val channelId: String,     // 채널 키
    val title: String,  // 제목
    val body: String,   // 본문
    val uri: Uri,       // 딥링크 URI
    val collapseKey: String?,    // 콜백 키
    val version: String = "1" // 스키마 버전
) {
    companion object {
        fun from(data: Map<String, String>): PushPayload? {
            val title = data[PushKeys.TITLE] ?: return null
            val ch = when (data[PushKeys.CHANNEL]) {
                PushKeys.Channel.REMINDER,
                PushKeys.Channel.PROGRESS,
                PushKeys.Channel.SUMMARY,
                PushKeys.Channel.ACTIVITY -> data[PushKeys.CHANNEL]!!

                else -> PushKeys.Channel.ACTIVITY
            }
            val uri = normalizeUri(data[PushKeys.URI])
            return PushPayload(
                channelId = ch,
                title = title,
                body = data[PushKeys.BODY].orEmpty(),
                uri = uri,
                collapseKey = data[PushKeys.COLLAPSE_KEY],
                version = data[PushKeys.VERSION] ?: "1"
            )
        }

        private fun normalizeUri(raw: String?): Uri {
            if (raw.isNullOrBlank()) return "gureum://home".toUri()

            val uri = raw.toUri()

            // buildUpon: URI의 속성을 복사해 새 빌더를 구성
            return if (uri.scheme == "app") uri.buildUpon().scheme("gureum").build() else uri
        }
    }
}