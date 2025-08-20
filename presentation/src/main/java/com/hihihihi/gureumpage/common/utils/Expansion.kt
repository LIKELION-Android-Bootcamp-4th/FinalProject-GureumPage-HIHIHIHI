package com.hihihihi.gureumpage.common.utils

fun String.validateNickname(): Boolean {
    val forbiddenWords = listOf("운영자", "관리자", "admin", "시발", "shit", "fuck", "병신") // 필요시 확장
    val nicknameRegex = Regex("^[가-힣a-zA-Z0-9]{2,8}$")

    return this.length in 2..8 &&
            !this.contains(" ") &&
            forbiddenWords.none { lowercase().contains(it) } &&
            nicknameRegex.matches(this)
}