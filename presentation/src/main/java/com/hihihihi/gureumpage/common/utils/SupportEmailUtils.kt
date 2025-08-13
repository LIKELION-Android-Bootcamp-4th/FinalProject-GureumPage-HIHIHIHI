package com.hihihihi.gureumpage.common.utils

import android.content.ActivityNotFoundException
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.widget.Toast

private fun appVersion(context: Context): Pair<String, Long> = try {
    val p = context.packageManager.getPackageInfo(context.packageName, 0)
    val name = p.versionName ?: "unKnown"
    val code = if (Build.VERSION.SDK_INT >= 28) p.longVersionCode else p.versionCode.toLong()
    name to code
} catch (_: Exception) {
    "unknown" to -1L
}

fun openSupportEmail(context: Context) {
    val (verName, verCode) = appVersion(context)
    val subject = "[GureumPage] 문의하기"
    val body = buildString {
        appendLine("아래 기본 정보를 함께 보내주시면 큰 도움이 됩니다 🙏")
        appendLine("— 앱 버전: $verName ($verCode)")
        appendLine("— 패키지: ${context.packageName}")
        appendLine("— 디바이스: ${Build.MANUFACTURER} ${Build.MODEL}")
        appendLine("— OS: Android ${Build.VERSION.RELEASE} (SDK ${Build.VERSION.SDK_INT})")
        appendLine()
        appendLine("문의 내용: (여기에 적어주세요)")
    }

    val intent = Intent(Intent.ACTION_SENDTO).apply {
        data = Uri.parse("mailto:") // SENDTO + mailto: 로 메일 앱만 타겟팅
        putExtra(Intent.EXTRA_EMAIL, arrayOf("support@example.com")) // TODO: 실제 지원 메일로 교체
        putExtra(Intent.EXTRA_SUBJECT, subject)
        putExtra(Intent.EXTRA_TEXT, body)
    }

    try {
        context.startActivity(intent)
    } catch (_: ActivityNotFoundException) {
        // 메일 앱이 없는 경우 대비: 본문 복사 + 안내
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        clipboard.setPrimaryClip(ClipData.newPlainText("문의 내용", "$subject\n\n$body"))
        Toast.makeText(context, "메일 앱이 없어 내용을 클립보드에 복사했어요.", Toast.LENGTH_LONG).show()
    }
}