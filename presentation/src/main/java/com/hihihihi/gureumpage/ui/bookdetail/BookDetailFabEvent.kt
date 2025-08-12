package com.hihihihi.gureumpage.ui.bookdetail

sealed class BookDetailFabEvent {
    object NavigateToMindmap : BookDetailFabEvent()
    object NavigateToTimer : BookDetailFabEvent()
    object ShowAddQuoteDialog : BookDetailFabEvent()
    object ShowAddReadingHistoryDialog : BookDetailFabEvent()}