package com.hihihihi.gureumpage.common.utils

import java.util.Locale

sealed interface NicknameRule {
    data object Empty : NicknameRule
    data object Length : NicknameRule
    data object IllegalChar : NicknameRule   // 허용 문자 외(공백, 특수문자, 이모지 등)
    data object InnerSpace : NicknameRule    // 글자 사이 공백
    data object ForbiddenWord : NicknameRule // 금칙어 포함
    data object Reserved : NicknameRule      // 예약어
    data object Ok : NicknameRule
}

object NicknameValidator {
    private val allowed = Regex("^[가-힣a-zA-Z0-9]{2,8}$")
    private val innerWhitespace = Regex("\\p{Z}|\\s")   // 일반 공백 + 제로폭 공백 등 유니코드 공백 모두

    private val forbiddenWords = listOf(
        "운영자", "관리자", "admin", "administrator", "manager", "master", "owner", "system", "sysop", "moderator", "mod", "staff",
        "시발", "씨발", "씹", "좆", "병신", "미친", "썅", "개새", "개색", "fuck", "shit", "bitch", "sex", "porn", "cum",
        "official", "dev", "developer", "tester", "qa", "cs", "support", "help", "guide"
    ) // 필요시 확장

    private val reserved = setOf("guest", "anonymous", "unknown", "null", "undefined", "user", "member")

    // 검증 후 NicknameRule 반환
    fun validate(raw: String): NicknameRule {
        val nickname = raw.nicknameNormalize()

        if (nickname.isEmpty()) return NicknameRule.Empty
        if (nickname.length !in 2..8) return NicknameRule.Length
        if (innerWhitespace.containsMatchIn(nickname)) return NicknameRule.InnerSpace
        if (!allowed.matches(nickname)) return NicknameRule.IllegalChar

        val lower = nickname.lowercase(Locale.ROOT) // 언어에 국한되지 않고 소문자 처리
        if (reserved.any { it == lower }) return NicknameRule.Reserved
        if (forbiddenWords.any { lower.contains(it) }) return NicknameRule.ForbiddenWord

        return NicknameRule.Ok
    }

    fun String.nicknameNormalize(): String =
        this.trim()
            .filterNot { it.isISOControl() }            // ISO 제어 문자 제한
            .replace("\u200B", "")  // 폭이 없는 공백 문자 제한
            .let { java.text.Normalizer.normalize(it, java.text.Normalizer.Form.NFKC) } // 유니코드 정규화

    // 검증 Boolean 값 반환
    fun String.validateNickname(): Boolean = NicknameValidator.validate(this) is NicknameRule.Ok
}
