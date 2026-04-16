package com.hihihihi.data.common.util

// ISBN 정규화 유틸리티 클래스
object ISBNNormalizer {

    /**
     * ISBN을 정규화합니다 (모든 ISBN을 ISBN-13으로 통일)
     * @param isbn 원본 ISBN (하이픈 포함/미포함 모두 가능)
     * @return 정규화된 ISBN-13 (하이픈 제거된 13자리 숫자)
     */
    fun normalize(isbn: String?): String? {
        if (isbn.isNullOrBlank()) return null

        // 하이픈, 공백 제거하고 대문자로 변환
        val cleanIsbn = isbn.replace(Regex("[\\s-]"), "").uppercase()

        return when {
            isValidISBN13(cleanIsbn) -> cleanIsbn
            isValidISBN10(cleanIsbn) -> convertISBN10ToISBN13(cleanIsbn)
            else -> null // 유효하지 않은 ISBN
        }
    }

    /**
     * ISBN-13인지 검증
     */
    private fun isValidISBN13(isbn: String): Boolean {
        if (isbn.length != 13) return false
        if (!isbn.all { it.isDigit() }) return false

        // ISBN-13 체크섬 검증
        val checkSum = isbn.dropLast(1)
            .mapIndexed { index, char ->
                val digit = char.digitToInt()
                if (index % 2 == 0) digit else digit * 3
            }
            .sum()

        val calculatedCheckDigit = (10 - (checkSum % 10)) % 10
        val actualCheckDigit = isbn.last().digitToInt()

        return calculatedCheckDigit == actualCheckDigit
    }

    /**
     * ISBN-10인지 검증
     */
    private fun isValidISBN10(isbn: String): Boolean {
        if (isbn.length != 10) return false

        // 마지막 문자는 X일 수 있음 (체크 디지트가 10인 경우)
        val digits = isbn.dropLast(1)
        if (!digits.all { it.isDigit() }) return false

        val lastChar = isbn.last()
        if (lastChar != 'X' && !lastChar.isDigit()) return false

        // ISBN-10 체크섬 검증
        val checkSum = digits.mapIndexed { index, char ->
            char.digitToInt() * (10 - index)
        }.sum()

        val calculatedCheckDigit = (11 - (checkSum % 11)) % 11
        val actualCheckDigit = when (lastChar) {
            'X' -> 10
            else -> lastChar.digitToInt()
        }

        return calculatedCheckDigit == actualCheckDigit
    }

    /**
     * ISBN-10을 ISBN-13으로 변환
     */
    private fun convertISBN10ToISBN13(isbn10: String): String {
        if (!isValidISBN10(isbn10)) throw IllegalArgumentException("Invalid ISBN-10: $isbn10")

        // ISBN-10에서 체크 디지트 제거
        val isbn10WithoutCheck = isbn10.dropLast(1)

        // 978 prefix 추가
        val isbn13WithoutCheck = "978$isbn10WithoutCheck"

        // ISBN-13 체크 디지트 계산
        val checkSum = isbn13WithoutCheck.mapIndexed { index, char ->
            val digit = char.digitToInt()
            if (index % 2 == 0) digit else digit * 3
        }.sum()

        val checkDigit = (10 - (checkSum % 10)) % 10

        return isbn13WithoutCheck + checkDigit
    }

    /**
     * 표시용 ISBN 포맷 (하이픈 추가)
     */
    fun formatForDisplay(isbn: String?): String? {
        val normalizedIsbn = normalize(isbn) ?: return null

        return if (normalizedIsbn.length == 13) {
            // ISBN-13 포맷: 978-0-123-45678-9
            "${normalizedIsbn.substring(0, 3)}-${
                normalizedIsbn.substring(
                    3,
                    4
                )
            }-${normalizedIsbn.substring(4, 7)}-${
                normalizedIsbn.substring(
                    7,
                    12
                )
            }-${normalizedIsbn.substring(12)}"
        } else {
            normalizedIsbn
        }
    }
}