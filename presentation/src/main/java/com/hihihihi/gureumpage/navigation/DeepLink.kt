package com.hihihihi.gureumpage.navigation

import androidx.core.net.toUri

object DeepLink {
    private const val SCHEME = "gureum"

    // NavHost 등록용 패턴
    const val HOME_PATTERN = "$SCHEME://home"
    const val BOOK_DETAIL_PATTERN = "$SCHEME://bookDetail/{bookId}"


    // 알림 진입용 패턴
    fun home() = "$SCHEME://home".toUri()
    fun bookDetail(bookId: String) = "$SCHEME://bookDetail/$bookId".toUri()
}