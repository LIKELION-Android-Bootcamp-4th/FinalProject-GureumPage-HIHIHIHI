package com.hihihihi.gureumpage.widgets.common

object DeepLinks {
    private const val SCHEME = "gureumpage"
    private const val HOST = "app"

    const val BOOK_DETAIL = "$SCHEME://$HOST/book"
    const val BOOK_MISSED_RECORD = "$SCHEME://$HOST/book/missedRecord"
    const val BOOK_TIMER = "$SCHEME://$HOST/book/timer"
    const val BOOK_ADD_QUOTE = "$SCHEME://$HOST/book/addQuote"

    const val OPEN_APP = "$SCHEME://$HOST"

    fun createBookDetailLink(bookId: String, from: String = "widget"): String = "$BOOK_DETAIL/$bookId?from=$from"

    fun createMissedRecordLink(bookId: String, from: String = "widget"): String = "$BOOK_MISSED_RECORD/$bookId?from=$from"

    fun createTimerLink(bookId: String, from: String = "widget"): String = "$BOOK_TIMER/$bookId?from=$from"

    fun createAddQuoteLink(bookId: String, from: String = "widget"): String = "$BOOK_ADD_QUOTE/$bookId?from=$from"

    fun createOpenAppLink(): String = OPEN_APP
}